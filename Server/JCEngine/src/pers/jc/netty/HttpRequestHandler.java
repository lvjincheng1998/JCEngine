package pers.jc.netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private String baseURI;

    public HttpRequestHandler(String baseURI) {
        this.baseURI = baseURI;
    }
   
	@Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
		if (
			request.uri().equals(baseURI) && 
			"websocket".equals(request.headers().get("Upgrade"))
		) {
			ctx.fireChannelRead(request.retain());
		} else {
			ctx.close();
		}
    }
}
