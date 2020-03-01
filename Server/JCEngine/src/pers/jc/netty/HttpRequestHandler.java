package pers.jc.netty;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private String websocketPath;

    public HttpRequestHandler(String websocketPath){
        this.websocketPath = websocketPath;
    }
   
	@SuppressWarnings("deprecation")
	@Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        if(websocketPath.equalsIgnoreCase(request.getUri())){
            ctx.fireChannelRead(request.retain());
        }else{
            ctx.close();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause){
        cause.printStackTrace();
        ctx.close();
    }
}
