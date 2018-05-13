package team.ecciot.server.example.comm;

import java.util.ArrayList;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import team.ecciot.lib.args.builder.CmdBuilder;
import team.ecciot.lib.args.callback.ICheckIdentityCallback;
import team.ecciot.lib.args.callback.IDevice2ServerEventCallback;
import team.ecciot.lib.args.callback.IRtc2ServerEventCallback;
import team.ecciot.lib.args.callback.ITerminal2ServerEventCallback;
import team.ecciot.lib.args.model.impl.APIKeyInvalidArgs;
import team.ecciot.lib.args.model.impl.APIKeyVerifiedArgs;
import team.ecciot.lib.args.model.impl.AlarmArgs;
import team.ecciot.lib.args.model.impl.AskIdentityArgs;
import team.ecciot.lib.args.model.impl.BindDeviceArgs;
import team.ecciot.lib.args.model.impl.CheckDeviceIdentityArgs;
import team.ecciot.lib.args.model.impl.CheckServerIdentityArgs;
import team.ecciot.lib.args.model.impl.CheckTerminalIdentityArgs;
import team.ecciot.lib.args.model.impl.CloseDeviceArgs;
import team.ecciot.lib.args.model.impl.CommunicationOutageArgs;
import team.ecciot.lib.args.model.impl.ControlDeviceArgs;
import team.ecciot.lib.args.model.impl.DeviceStateChangedArgs;
import team.ecciot.lib.args.model.impl.ReturnDeviceQueryResultArgs;
import team.ecciot.lib.args.model.impl.ReturnTerminalQueryResultArgs;
import team.ecciot.lib.args.model.impl.TerminalStateChangedArgs;
import team.ecciot.lib.args.parser.ContentParser;
import team.ecciot.server.example.Config;
import team.ecciot.server.example.manager.Group;
import team.ecciot.server.example.manager.GroupManager;
import team.ecciot.server.example.model.DeviceModel;
import team.ecciot.server.example.model.TerminalModel;
import team.ecciot.server.example.utils.ChannelUtils;

public class ClientHandler extends SimpleChannelInboundHandler<String>
		implements IRtc2ServerEventCallback, IDevice2ServerEventCallback, ITerminal2ServerEventCallback {

	// 是否已完成身份验证
	boolean bCheckIdentityFinished = false;

	public static Logger LOGGER = LogManager.getLogger(ClientHandler.class);

	// 参数内容解析器
	ContentParser parser;

	// 回调处理器
	ClientHandler callbackHandler;

	public ClientHandler() {
		callbackHandler = this;
	}

	// 与RTC通信的信道
	private Channel channelRtc;

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		channelRtc = ctx.channel();

		System.out.println("收到消息：" + msg);

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
						// 切换解析器
						parser = new ContentParser(callbackHandler);
					}

					@Override
					public void RTC_APIKeyInvalid(APIKeyInvalidArgs args) {
						LOGGER.info("RTC否认了身份信息，请校验apikey和密匙。信息：" + args.getMessage());
					}

					@Override
					public void InvalidActionInstruction(String action, String content) {

					}
				}, ICheckIdentityCallback.class);
				// 执行解析
				parser.parse(action, content);
			} else {
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

	@Override
	public void InvalidActionInstruction(String action, String content) {
	}

	@Override
	public void Terminal_ControlDevice(ControlDeviceArgs args) {
		LOGGER.info("终端控制设备命令。args:" + args);
		//这里有安全隐患，需要获取发送方的信息验证权限（是否在同一个组内）
		//命令中附带的token可以在rtc中被验证，但是这样无法弥补自定义命令的安全性问题
		//解决方法应该是在rtc转发数据时进行包装
		
		//不安全：进行直接转发
		String itemId = args.getItemID();
		JSONObject json = CmdBuilder.build(args, "device", itemId);
		ChannelUtils.sendMessage(channelRtc, json.toJSONString());
	}

	@Override
	public void Terminal_BindDevice(BindDeviceArgs args) {
		LOGGER.info("终端绑定设备命令。args:" + args);
	}

	@Override
	public void Device_Alarm(AlarmArgs args) {
		LOGGER.info("设备警报命令。args:" + args);
	}

	@Override
	public void RTC_DeviceStateChanged(DeviceStateChangedArgs args) {
		LOGGER.info("RTC连接设备状态发生改变。args:" + args);
		if (args.isState()) {
			/* 新设备上线 */
			// 获取设备属性
			CheckDeviceIdentityArgs cdia = args.getDeviceInfo();
			// 通过设备的itemid查找设备对应的GroupId
			String groupId = GroupManager.getInstance().getDevice4GroupId().get(cdia.getItemID());
			if (groupId == null) {
				LOGGER.info("新设备尚未注册，拒绝通信。itmeId:" + cdia.getItemID());
				// 构建设备状态控制指令
				CloseDeviceArgs cda = new CloseDeviceArgs();
				cda.setItemID(cdia.getItemID());
				cda.setState(false);
				JSONObject json = CmdBuilder.build(cda);
				ChannelUtils.sendMessage(channelRtc, json.toJSONString());
				return;
			}
			// 通过groupId获取Group
			Group group = GroupManager.getInstance().getGroupById(groupId);
			// 创建信息的设备模型
			DeviceModel dm = new DeviceModel();
			dm.setItemID(cdia.getItemID());
			dm.setModel(cdia.getModel());
			dm.setVersion(cdia.getVersion());
			dm.setData(new JSONObject());
			dm.setState(true);
			// 将设备模型加入group
			group.getDeviceModelList().add(dm);
			LOGGER.info(String.format("用户组[%s] - 设备[%s]已上线。", groupId, cdia.getItemID()));
		} else {
			/* 设备已下线 */
			CheckDeviceIdentityArgs cdia = args.getDeviceInfo();
			// 通过设备的itemid查找设备对应的GroupId
			String groupId = GroupManager.getInstance().getDevice4GroupId().get(cdia.getItemID());
			if (groupId == null) {
				LOGGER.info("未知设备已退出通信。args:" + args);
				return;
			}
			// 通过groupId获取Group
			Group group = GroupManager.getInstance().getGroupById(groupId);
			// 获取设备模型
			DeviceModel dm = group.getDeviceById(cdia.getItemID());
			dm.setState(false);
			LOGGER.info(String.format("用户组[%s] - 设备[%s]已离线。", groupId, cdia.getItemID()));
		}
	}

	@Override
	public void RTC_ReturnDeviceQueryResult(ReturnDeviceQueryResultArgs args) {
		LOGGER.info("RTC返回当前在线设备列表。args:" + args);
	}

	@Override
	public void RTC_ReturnTerminalQueryResult(ReturnTerminalQueryResultArgs args) {
		LOGGER.info("RTC返回当前在线终端列表。args:" + args);
	}

	@Override
	public void RTC_CommunicationOutage(CommunicationOutageArgs args) {
		LOGGER.info("RTC主动中断了通信，信息：" + args.getMessage());
	}

	@Override
	public void RTC_TerminalStateChanged(TerminalStateChangedArgs args) {
		LOGGER.info("RTC连接设备状态发生改变。args:" + args);
		if (args.isState()) {
			/* 新设备上线 */
			// 获取设备属性
			CheckTerminalIdentityArgs ctia = args.getTerminalInfo();
			// 通过设备的itemid查找设备对应的GroupId
			String groupId = GroupManager.getInstance().getTermianl4GroupId().get(ctia.getToken());
			if (groupId == null) {
				LOGGER.info("新终端尚未注册，拒绝通信。itmeId:" + ctia.getToken());
				return;
			}
			// 通过groupId获取Group
			Group group = GroupManager.getInstance().getGroupById(groupId);
			// 创建信息的设备模型
			TerminalModel tm = new TerminalModel();
			tm.setToken(ctia.getToken());
			tm.setVersion(ctia.getVersion());
			tm.setState(true);
			// 将设备模型加入group
			group.getTerminalModelList().add(tm);
			LOGGER.info(String.format("用户组[%s] - 终端[%s]已上线。", groupId, ctia.getToken()));
		} else {
			/* 设备已下线 */
			CheckTerminalIdentityArgs cdia = args.getTerminalInfo();
			// 通过设备的  查找设备对应的GroupId
			String groupId = GroupManager.getInstance().getTermianl4GroupId().get(cdia.getToken());
			if (groupId == null) {
				LOGGER.info("未知终端已退出通信。args:" + args);
				return;
			}
			// 通过groupId获取Group
			Group group = GroupManager.getInstance().getGroupById(groupId);
			// 获取设备模型
			TerminalModel tm = group.getTerminalById(cdia.getToken());
			tm.setState(false);
			LOGGER.info(String.format("用户组[%s] - 终端[%s]已离线。", groupId, cdia.getToken()));
		}
	}
}
