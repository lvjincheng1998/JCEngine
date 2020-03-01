package pers.jc.engine;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class JCEntity {
	public int id;
	public Channel channel;
	public boolean isValid;
	
	public void onLoad() {}	
	
	public void onDestroy() {}
	
	public void call(String func, Object... args) {
		if (isValid) {
			String text = new JCData(id, JCData.TYPE_FUNCTION, func, args).stringify();
			channel.writeAndFlush(new TextWebSocketFrame(text));
		}
	}
}
