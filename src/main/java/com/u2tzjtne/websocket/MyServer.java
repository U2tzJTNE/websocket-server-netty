package com.u2tzjtne.websocket;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @Author u2tzjtne@gmail.com
 * @Description Netty WebSocket长连接初始化类
 * @Date 2023年3月7日10:17:41
 */
public class MyServer {
    public static void main(String[] args) throws InterruptedException {
        // 创建两个线程组
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    // 在workerGroup中增加处理器
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            System.out.println("收到新连接:" + ch.localAddress());
                            ChannelPipeline pipeline = ch.pipeline();
                            //HTTP 协议解析，用于握手阶段
                            pipeline.addLast(new HttpServerCodec());
                            pipeline.addLast(new ChunkedWriteHandler());
                            //HTTP 协议解析，用于握手阶段
                            pipeline.addLast(new HttpObjectAggregator(8192));
                            //WebSocket 数据压缩扩展
//                            pipeline.addLast(new WebSocketServerCompressionHandler());
                            /*
                              对应的webSocket，它的数据是以帧（frame）的形式传递的。
                              浏览器发送webSocket请求时，使用ws协议，如 ws://localhost:1234/hello。
                              WebSocketServerProtocolHandler 核心功能是将http协议升级为ws协议，保持长连接
                             */
                            pipeline.addLast(new WebSocketServerProtocolHandler("/socket", "WebSocket", true, 65536 * 10));
                            // 自定义handler，处理业务逻辑
                            pipeline.addLast(new MyServerHandler());
                        }
                    });
            //端口设置
            ChannelFuture channelFuture = bootstrap.bind(8888).sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
