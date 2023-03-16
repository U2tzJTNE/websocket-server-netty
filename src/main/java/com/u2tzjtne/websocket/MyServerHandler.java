package com.u2tzjtne.websocket;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
/**
 * @Author u2tzjtne@gmail.com
 * @Description Netty WebSocket长连接处理类
 * @Date 2023/3/7 10:40
 */
public class MyServerHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    /**
     * TextWebSocketFrame 表示一个文本帧（frame）
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) {
        System.out.println("服务器收到消息 ==> " + msg.text());
        // 返回数据  LocalDateTime.now()表示获取当前时间
        ctx.writeAndFlush(new TextWebSocketFrame(msg.text()));
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        // 获取channel唯一值，asLongText方法是channel的id的全名
        System.out.println("handlerAdded被调用 " + ctx.channel().id().asLongText());
        // asShortText方法是获取channel的id短名，是全名末尾一段的截取
        System.out.println("handlerAdded被调用 " + ctx.channel().id().asShortText());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        System.out.println("handlerRemoved被调用 " + ctx.channel().id().asLongText());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.out.println("异常发生 ");
        cause.printStackTrace();
        ctx.channel().close();
    }
}
