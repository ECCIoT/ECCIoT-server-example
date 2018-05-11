package team.ecciot.server.example.utils;

import io.netty.channel.Channel;

public class ChannelUtils {
	public static void sendMessage(Channel channel,String msg){
		channel.write(msg);
		channel.writeAndFlush("\n");
	}
}
