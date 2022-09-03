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
	public long heartBeatTimeRecord;
    
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
			Dispatcher.handleSocketFunction(tempEntity, data);
		} else if (data.getType() == JCData.TYPE_METHOD) {
			Dispatcher.handleSocketMethod(tempEntity, data);
		}
	}

	@SocketEvent
    public void loadTempEntity() throws Exception {
    	tempEntity = JCEngine.entityClass.newInstance();
		tempEntity.channel = new JCChannel(channel);
		tempEntity.isValid = true;
		tempEntity.authed = JCEngine.defaultAuthValue;
		call("loadTempEntity", tempEntity.id);
		JCEngine.gameService.execute(tempEntity::onLoad);
		HeartBeatHandler.ins.addEntity(this);
    }
    
    private void destroyTempEntity() {
		if (tempEntity != null) {
			JCEngine.gameService.execute(() -> {
				tempEntity.isValid = false;
				tempEntity.onDestroy();
			});
		}
    }

	@SocketEvent
	public void doHeartBeat() {
		heartBeatTimeRecord = System.currentTimeMillis();
	}

	public void die() {
		channel.close();
	}
}
