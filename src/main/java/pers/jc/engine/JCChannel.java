package pers.jc.engine;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class JCChannel {
	private Channel channel;
	
	public JCChannel(Channel channel) {
		this.channel = channel;
	}
	
	public void writeAndFlush(String text) {
		this.channel.writeAndFlush(new TextWebSocketFrame(text));
	}
	
	public void close() {
		this.channel.close();
	}
}
