package com.u2tzjtne.websocket;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @Author u2tzjtne@gmail.com
 * @Description Netty WebSocket长连接处理类
 * @Date 2023/3/7 10:40
 */
public class MyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    public static ChannelGroup channelGroup;

    static {
        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    }

    //客户端与服务器建立连接的时候触发，
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().id() + "建立连接");
        //添加到channelGroup通道组
        channelGroup.add(ctx.channel());
    }

    //客户端与服务器关闭连接的时候触发，
    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        System.out.println(ctx.channel().id() + "断开连接");
        channelGroup.remove(ctx.channel());
    }

    //服务器接受客户端的数据信息，
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println("服务器收到的数据：" + msg.text());
        //sendMessage(ctx);
        sendAllMessage(ctx.channel(), msg.text());
    }

    //给固定的人发消息
    private void sendMessage(Channel channel, String message) {
        channel.writeAndFlush(new TextWebSocketFrame(message));
    }

    //发送群消息,此时其他客户端也能收到群消息
    private void sendAllMessage(Channel channel, String message) {
        channelGroup.writeAndFlush(new TextWebSocketFrame(message), ch -> ch.id() != channel.id());
    }
}
