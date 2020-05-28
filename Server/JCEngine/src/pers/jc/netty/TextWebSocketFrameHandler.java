package pers.jc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import pers.jc.engine.JCChannel;
import pers.jc.engine.JCData;
import pers.jc.engine.JCEngine;
import pers.jc.engine.JCEntity;
import pers.jc.mvc.ControllerHandler;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
	private Channel channel;
    private JCEntity tempEntity;
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
    	if (channel == null) {
    		channel = ctx.channel();
    	} 
    	invoke(msg.text());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	destroyTempEntity();
    }
    
    private void call(String func, Object... args) {
    	String text = new JCData("", JCData.TYPE_EVENT, func, args).stringify();
		channel.writeAndFlush(new TextWebSocketFrame(text));
    }
    
    private void invoke(String text) throws Exception {
    	JCData data = JCData.parse(text);
    	String func = data.getFunc();
		Object[] args = data.getArgs();
		Class<?>[] argTypes = new Class[args.length];
		for (int i = 0; i < argTypes.length; i++) {
			argTypes[i] = args[i].getClass();
		}
    	if (data.getType() == JCData.TYPE_EVENT) {
    		getClass().getMethod(func, argTypes).invoke(this, args);
    		return;
		}
    	if (data.getType() == JCData.TYPE_FUNCTION) {
    		if (tempEntity.isValid) {
    			tempEntity.getClass().getMethod(func, argTypes).invoke(tempEntity, args);
    		}
			return;
		} 
    	if (data.getType() == JCData.TYPE_METHOD) {
    		JCData resData = ControllerHandler.handle(tempEntity, data);
    		if (resData == null) {
    			return;
    		} else {
    			channel.writeAndFlush(new TextWebSocketFrame(resData.stringify()));
    		}
		} 
	}
    
    public void loadTempEntity() throws Exception {
    	tempEntity = JCEngine.entityClass.newInstance();
		tempEntity.id = JCEngine.generateId();
		tempEntity.channel = new JCChannel(channel);
		tempEntity.isValid = true;
		call("loadTempEntity", tempEntity.id);
		tempEntity.onLoad();
    }
    
    public void destroyTempEntity() {
		if (tempEntity != null) {
			tempEntity.isValid = false;
			tempEntity.onDestroy();
		}
    }
}
