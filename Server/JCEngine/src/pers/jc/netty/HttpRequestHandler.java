package pers.jc.netty;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private String webSocketPath;

    public HttpRequestHandler(String webSocketPath) {
        this.webSocketPath = webSocketPath;
    }
   
	@SuppressWarnings("deprecation")
	@Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        if (webSocketPath.equalsIgnoreCase(request.getUri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
