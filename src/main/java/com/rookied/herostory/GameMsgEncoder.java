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
            int msgCode = -1;
            if (msg instanceof GameMsgProtocol.UserEntryResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.WhoElseIsHereResult) {
                msgCode = GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.UserMoveToResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE;
            } else if (msg instanceof GameMsgProtocol.UserQuitResult) {
                msgCode = GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE;
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
