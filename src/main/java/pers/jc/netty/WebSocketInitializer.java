package pers.jc.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import pers.jc.engine.JCEngine;

public class WebSocketInitializer extends ChannelInitializer<SocketChannel> {
    private final String path;

    protected WebSocketInitializer(String path) {
        this.path = path;
    }

    @Override
    protected void initChannel(SocketChannel ch) {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(JCEngine.maxContentLength));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new HttpRequestHandler(path));
        pipeline.addLast(new WebSocketServerProtocolHandler(path));
        pipeline.addLast(new WebSocketHandler());
    }
}