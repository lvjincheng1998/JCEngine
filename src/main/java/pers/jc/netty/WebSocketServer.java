package pers.jc.netty;

import java.net.InetAddress;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import pers.jc.sql.DataView;
import pers.jc.util.JCLogger;

public class WebSocketServer {
	
    public static void run(int port, String path) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new WebSocketInitializer(path))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(port).sync();
            JCLogger.info("JCEngine Server Listen At "
            		+ InetAddress.getLocalHost().getHostAddress()
            		+ ":" + port + path);
            if (DataView.enabled) {
                JCLogger.info("Data View Listen At "
                    + InetAddress.getLocalHost().getHostAddress()
                    + ":" + port + path + "/data-view/index.html");
            }
            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
