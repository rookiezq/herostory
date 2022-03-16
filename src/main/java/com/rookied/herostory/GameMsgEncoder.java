package com.rookied.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.rookied.herostory.msg.GameMsgProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.EmptyByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rookied
 * @date 2022.03.07
 */
public class GameMsgEncoder extends ChannelOutboundHandlerAdapter {
    static private final Logger LOG = LoggerFactory.getLogger(GameMsgEncoder.class);

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (null == ctx || null == msg) {
            return;
        }

        try {
            if (!(msg instanceof GeneratedMessageV3)) {
                super.write(ctx, msg, promise);
                return;
            }

            //将result转为BinaryWebSocketFrame
            int msgCode = GameMsgRecoginzer.getMsgCodeByClazz(msg.getClass());
            if (-1 == msgCode) {
                LOG.error(
                        "无法识别的消息类型, msgClazz = {}",
                        msg.getClass().getSimpleName()
                );
                super.write(ctx, msg, promise);
                return;
            }

            //消息体
            byte[] msgBody = ((GeneratedMessageV3) msg).toByteArray();
            ByteBuf byteBuf = ctx.alloc().buffer();
            //长度
            byteBuf.writeShort((short) msgBody.length);
            //消息编码
            byteBuf.writeShort((short) msgCode);
            byteBuf.writeBytes(msgBody);

            BinaryWebSocketFrame outputFrame = new BinaryWebSocketFrame(byteBuf);
            super.write(ctx, outputFrame, promise);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }


    }
}
