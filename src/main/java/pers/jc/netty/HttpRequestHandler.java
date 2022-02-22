package pers.jc.netty;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String path;

    protected HttpRequestHandler(String path) {
        this.path = path;
    }
   
	@Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
		if (request.headers().contains("Sec-WebSocket-Key") && request.uri().equals(path)) {
            ctx.fireChannelRead(request.retain());
		} else {
            FullHttpResponse response = new DefaultFullHttpResponse(
                    HttpVersion.HTTP_1_1, HttpResponseStatus.INTERNAL_SERVER_ERROR);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }
}
