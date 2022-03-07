package com.rookied.herostory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * @author rookied
 * @date 2022.03.05
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    static private final Logger LOG = LoggerFactory.getLogger(GameMsgHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (null ==ctx || null == msg) return;
        LOG.info("收到客户端消息, msg = {}" , msg);

        /*// WebSocket 二进制消息会通过 HttpServerCodec 解码成 BinaryWebSocketFrame 类对象
        BinaryWebSocketFrame frame = (BinaryWebSocketFrame)msg;
        ByteBuf byteBuf = frame.content();

        // 拿到真实的字节数组并打印
        byte[] byteArray = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(byteArray);

        LOG.info("收到的字节 = {}" , Arrays.toString(byteArray) + "\n");*/

    }
}
