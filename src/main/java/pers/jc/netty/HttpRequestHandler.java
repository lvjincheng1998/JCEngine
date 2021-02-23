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
import pers.jc.network.*;
import pers.jc.network.HttpRequest;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.*;

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
            if (HttpResource.check(httpRequest.getUri())) {
                returnResource(ctx, httpRequest.getUri());
                return;
            }
            Object handleResult = Dispatcher.handleHttpRequest(httpRequest);
            if (handleResult instanceof HttpResource) {
                HttpResource httpResource = (HttpResource) handleResult;
                if (HttpResource.check(httpResource.getUri())) {
                    returnResource(ctx, httpResource.getUri());
                }
                return;
            }
            if (handleResult instanceof HttpRedirect) {
                HttpRedirect httpRedirect = (HttpRedirect) handleResult;
                if (httpRedirect.getUrl().startsWith("http")) {
                    redirect(ctx, httpRedirect.getUrl());
                } else {
                    redirect(ctx, path + httpRedirect.getUrl());
                }
                return;
            }
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
        } else if (request.uri().endsWith("/favicon.ico")) {
            returnResource(ctx, "/favicon.ico");
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

    private void returnResource(ChannelHandlerContext ctx, String uri) throws Exception {
        InputStream inputStream = getClass().getResourceAsStream(uri);
        FullHttpResponse response;
        if (inputStream != null) {
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            byte[] bytes = new byte[bufferedInputStream.available()];
            bufferedInputStream.read(bytes);
            bufferedInputStream.close();
            ByteBuf bufContent;
            if (HttpResource.isByteType(uri)) {
                bufContent = copiedBuffer(bytes);
            } else {
                String strContent = new String(bytes);
                bufContent = copiedBuffer(strContent, CharsetUtil.UTF_8);
            }
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, bufContent);
        } else {
            response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND);
        }
        String contentType = HttpResource.getContentType(uri);
        response.headers().set("Content-Type", contentType);
        response.headers().set("Content-Length", response.content().readableBytes());
        response.headers().set("Access-Control-Allow-Origin", "*");
        response.headers().set("Cache-Control", "no-cache");
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    private void redirect(ChannelHandlerContext ctx, String url) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.TEMPORARY_REDIRECT); //设置重定向响应码 （临时重定向、永久重定向）
        HttpHeaders headers = response.headers();
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_HEADERS, "x-requested-with,content-type");
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_METHODS, "POST,GET");
        headers.set(HttpHeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
        headers.set(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        headers.set(HttpHeaderNames.LOCATION, url);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
