package pers.jc.netty;

import static io.netty.buffer.Unpooled.copiedBuffer;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.*;
import io.netty.util.CharsetUtil;
import pers.jc.network.Dispatcher;
import pers.jc.network.HttpRequest;
import pers.jc.network.HttpType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final String path;

    public HttpRequestHandler(String path) {
        this.path = path;
    }
   
	@Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
		if (request.headers().contains("Sec-WebSocket-Key") && request.uri().equals(path)) {
			ctx.fireChannelRead(request.retain());
		} else if (request.method() == HttpMethod.GET && request.uri().startsWith(path))  {
            HttpRequest httpRequest = new HttpRequest(request.uri().substring(path.length()), HttpType.GET, getGetParamMap(request));
            Object handleResult = Dispatcher.handleHttpRequest(httpRequest);
            if (handleResult == HttpRequest.URI_NOT_MATCH || handleResult == HttpRequest.TYPE_NOT_MATCH) {
                writeResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
            } else {
                writeResponse(ctx, HttpResponseStatus.OK, handleResult);
            }
		} else if (request.method() == HttpMethod.POST && request.uri().startsWith(path)) {
            HttpRequest httpRequest = new HttpRequest(request.uri().substring(path.length()), HttpType.POST, getPostParamMap(request));
            Object handleResult = Dispatcher.handleHttpRequest(httpRequest);
            if (handleResult == HttpRequest.URI_NOT_MATCH || handleResult == HttpRequest.TYPE_NOT_MATCH) {
                writeResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
            } else {
                writeResponse(ctx, HttpResponseStatus.OK, handleResult);
            }
        } else {
            writeResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    private Map<String, Object> getGetParamMap(FullHttpRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
        Map<String, List<String>> paramList = decoder.parameters();
        for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
            paramMap.put(entry.getKey(), entry.getValue().get(0));
        }
        return paramMap;
    }

    private Map<String, Object> getPostParamMap(FullHttpRequest request) {
        Map<String, Object> paramMap = new HashMap<>();
        String contentType = request.headers().get("Content-Type");
        if (contentType.contains("x-www-form-urlencoded")) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            List<InterfaceHttpData> httpData = decoder.getBodyHttpDatas();
            for (InterfaceHttpData data : httpData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    paramMap.put(attribute.getName(), attribute.getValue());
                }
            }
        } else if (contentType.contains("application/json")) {
            try {
                ByteBuf content = request.content();
                byte[] byteContent = new byte[content.readableBytes()];
                request.content().readBytes(byteContent);
                String strContent = new String(byteContent, CharsetUtil.UTF_8);
                JSONObject jsonObject = JSONObject.parseObject(strContent);
                for (Map.Entry<String, Object> entry : jsonObject.getInnerMap().entrySet()) {
                    paramMap.put(entry.getKey(), entry.getValue());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (contentType.contains(HttpHeaderValues.MULTIPART_FORM_DATA)) {
            HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
            List<InterfaceHttpData> httpData = decoder.getBodyHttpDatas();
            for (InterfaceHttpData data : httpData) {
                if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                    MemoryAttribute attribute = (MemoryAttribute) data;
                    paramMap.put(attribute.getName(), attribute.getValue());
                } else if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.FileUpload) {
                    FileUpload fileUpload = (FileUpload) data;
                    if (fileUpload.isCompleted()) {
                        paramMap.put(fileUpload.getName(), fileUpload);
                    }
                }
            }
        }
        return paramMap;
    }

    private void writeResponse(ChannelHandlerContext ctx, HttpResponseStatus status, Object content) {
        String strContent = content == null ? "" : JSON.toJSONString(content, SerializerFeature.DisableCircularReferenceDetect);
        ByteBuf bufContent = copiedBuffer(strContent, CharsetUtil.UTF_8);
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, bufContent);
        if (content == null || content instanceof String) {
            response.headers().set("Content-Type", "text/plain; charset=UTF-8");
        } else {
            response.headers().set("Content-Type", "application/json; charset=utf-8");
        }
        response.headers().set("Content-Length", response.content().readableBytes());
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set("Cache-Control", "no-cache");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
