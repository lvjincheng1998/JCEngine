package pers.jc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import pers.jc.engine.JCEngine;
import pers.jc.engine.JCEntity;
import pers.jc.util.JCLogger;

public class TextWebSocketFrameHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
    private JCEntity entity;
    
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
    	if (entity != null) {
    		entity.invoke(msg.text());
    	} else {
    		synchronized (this) {
    			entity = JCEngine.entityClass.newInstance();
				entity.id = JCEngine.generateId();
        		entity.channel = ctx.channel();
        		entity.isValid = true;
        		entity.call("init", new Object[]{entity.id});
        		JCEngine.entities.put(entity.id, entity);
        		JCLogger.info("Entity:" + entity.id + " Created");
        		entity.onLoad();
			}
    	}
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
    	synchronized (this) {
			if(entity != null){
				entity.isValid = false;
				JCEngine.entities.remove(entity.id, entity);
				JCLogger.info("Entity:" + entity.id + " Destroyed");
				entity.onDestroy();
			}
    	}
    }
}
