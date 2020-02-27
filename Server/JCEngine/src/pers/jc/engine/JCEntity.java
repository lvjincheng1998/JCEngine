package pers.jc.engine;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

public class JCEntity {
	public int id;
	public Channel channel;
	public boolean isValid;
	
	public void onLoad() {}
	
	public void onDestroy() {}
	
	public void call(String funcName, Object... args) {
		if (isValid) {
			String msg = new JCData(funcName, args).stringify();
			channel.writeAndFlush(new TextWebSocketFrame(msg));
		}
	}
	
	public void invoke(String msg) throws Exception {
		if (isValid) {
			JCData jcData = JCData.parse(msg);
			String funcName = jcData.getFuncName();
			Object[] args = jcData.getArgs();
			Class<?>[] argTypes = new Class[args.length];
			for (int i = 0; i < argTypes.length; i++) {
				argTypes[i] = args[i].getClass();
			}
			this.getClass().getMethod(funcName, argTypes).invoke(this, args);
		}
	}
}
