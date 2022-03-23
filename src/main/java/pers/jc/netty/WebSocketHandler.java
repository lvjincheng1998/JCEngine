package pers.jc.netty;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import pers.jc.engine.JCChannel;
import pers.jc.engine.JCData;
import pers.jc.engine.JCEngine;
import pers.jc.engine.JCEntity;
import pers.jc.network.Dispatcher;
import pers.jc.network.SocketEvent;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {
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
    	if (data.getType() == JCData.TYPE_EVENT) {
			Dispatcher.handleSocketEvent(this, data);
		} else if (data.getType() == JCData.TYPE_FUNCTION) {
			tempEntity.director.requestHandler.offerRequest(tempEntity, data);
		} else if (data.getType() == JCData.TYPE_METHOD) {
			boolean isAsync = Dispatcher.checkAndAsyncDoneSocketMethod(tempEntity, data);
			if (!isAsync) {
				tempEntity.director.requestHandler.offerRequest(tempEntity, data);
			}
		}
	}

	@SocketEvent
    public void loadTempEntity() throws Exception {
    	tempEntity = JCEngine.entityClass.newInstance();
		tempEntity.director = JCEngine.director;
		tempEntity.channel = new JCChannel(channel);
		tempEntity.isValid = true;
		tempEntity.director.callbackHandler.offerCallback(() -> {
			call("loadTempEntity", tempEntity.id);
			tempEntity.onLoad();
		});
    }
    
    private void destroyTempEntity() {
		if (tempEntity != null) {
			tempEntity.director.callbackHandler.offerCallback(() -> {
				tempEntity.isValid = false;
				tempEntity.onDestroy();
			});
		}
    }
}
