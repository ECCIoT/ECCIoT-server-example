package team.ecciot.server.example.comm;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class CommClient {
	Bootstrap bootstrap;
	Channel channel;
	
	public void connect(int port, String host) throws Exception {
		EventLoopGroup group = new NioEventLoopGroup();
		try {
			bootstrap = new Bootstrap();
			bootstrap.group(group);
			bootstrap.channel(NioSocketChannel.class);
			bootstrap.option(ChannelOption.TCP_NODELAY, true);
			bootstrap.handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
			        pipeline.addLast("docode",new StringDecoder());
			        pipeline.addLast("encode",new StringEncoder());
			        pipeline.addLast("rtc",new ClientHandler());
				}
			});
			ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
			channel = channelFuture.channel();
			channel.closeFuture().sync();
		} finally {
			group.shutdownGracefully();
		}
	}
	
	public void send(String msg){
		channel.write(msg);
		channel.writeAndFlush("\n");
	}
}
