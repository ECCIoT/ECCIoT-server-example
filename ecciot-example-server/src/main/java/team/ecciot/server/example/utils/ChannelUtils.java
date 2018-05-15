package team.ecciot.server.example.utils;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import io.netty.channel.Channel;

public class ChannelUtils {
	
	public static Logger LOGGER = LogManager.getLogger(ChannelUtils.class);
	
	public static void sendMessage(Channel channel,String msg){
		LOGGER.info("[发送数据] → msg:" + msg);
		channel.write(msg);
		channel.writeAndFlush("\n");
	}
}
