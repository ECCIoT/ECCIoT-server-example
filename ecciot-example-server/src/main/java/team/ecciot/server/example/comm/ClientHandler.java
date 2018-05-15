package team.ecciot.server.example.comm;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import team.ecciot.lib.args.builder.CmdBuilder;
import team.ecciot.lib.args.callback.ICheckIdentityCallback;
import team.ecciot.lib.args.model.impl.APIKeyInvalidArgs;
import team.ecciot.lib.args.model.impl.APIKeyVerifiedArgs;
import team.ecciot.lib.args.model.impl.AskIdentityArgs;
import team.ecciot.lib.args.model.impl.CheckServerIdentityArgs;
import team.ecciot.lib.args.model.impl.CommunicationOutageArgs;
import team.ecciot.lib.args.parser.ContentParser;
import team.ecciot.server.example.Config;
import team.ecciot.server.example.utils.ChannelUtils;

public class ClientHandler extends SimpleChannelInboundHandler<String>{

	// 是否已完成身份验证
	boolean bCheckIdentityFinished = false;

	public static Logger LOGGER = LogManager.getLogger(ClientHandler.class);

	// 参数内容解析器
	ContentParser parser;

	// 与RTC通信的信道
	private Channel channelRtc;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		channelRtc = ctx.channel();

		LOGGER.info("[接收消息] ← msg:" + msg);

		// 将读取的指令转换为json格式
		JSONObject json = JSON.parseObject(msg);
		// 读取指令中的Action和Content
		String action = json.getString("action");
		String content = json.getString("content");

		// 判断action是否有效
		if (ContentParser.checkActionValidity(action)) {

			// 判断身份是否已完成验证
			if (!bCheckIdentityFinished) {
				parser = new ContentParser(new ICheckIdentityCallback() {

					@Override
					public void RTC_CommunicationOutage(CommunicationOutageArgs args) {
						LOGGER.info("RTC主动中断了通信，信息：" + args.getMessage());
					}

					@Override
					public void RTC_AskIdentity(AskIdentityArgs args) {
						LOGGER.info("RTC请求身份验证，信息：" + args.getMessage());

						CheckServerIdentityArgs csia = new CheckServerIdentityArgs();
						csia.setApikey(Config.API_KEY);
						csia.setSecretkey(Config.SECRET_KEY);
						JSONObject json = CmdBuilder.build(csia);
						ChannelUtils.sendMessage(channelRtc, json.toJSONString());
					}

					@Override
					public void RTC_APIKeyVerified(APIKeyVerifiedArgs args) {
						LOGGER.info("RTC身份验证已通过，信息：" + args);
						// 标记为已验证状态
						bCheckIdentityFinished = true;
						//切换解析回调处理器
						parser = new ContentParser(new ParserCallbackHandler(channelRtc),ParserCallbackHandler.class);
					}

					@Override
					public void RTC_APIKeyInvalid(APIKeyInvalidArgs args) {
						LOGGER.info("RTC否认了身份信息，请校验apikey和密匙。信息：" + args.getMessage());
					}

					@Override
					public void InvalidActionInstruction(String action, String content) {

					}
				}, ICheckIdentityCallback.class);
				// 执行身份验证解析
				parser.parse(action, content);
			} else {
				// 执行业务逻辑解析
				parser.parse(action, content);
			}
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
	}

}
