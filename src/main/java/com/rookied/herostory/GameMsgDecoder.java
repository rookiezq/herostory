package com.rookied.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.rookied.herostory.msg.GameMsgProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 自定义解码器
 * 消息格式 消息体长度（2字节）+消息编号（2字节）+消息体
 *
 * @author rookied
 * @date 2022.03.05
 */
public class GameMsgDecoder extends ChannelInboundHandlerAdapter {
    static private final Logger LOG = LoggerFactory.getLogger(GameMsgDecoder.class);

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg){
        //instanceof自动判空
        if (null == ctx || !(msg instanceof BinaryWebSocketFrame)) {
            return;
        }

        try {
            BinaryWebSocketFrame frame = (BinaryWebSocketFrame) msg;
            ByteBuf content = frame.content();
            content.readShort();//读取消息长度
            int msgCode = content.readShort();//读取消息编号
            //消息体
            byte[] msgBody = new byte[content.readableBytes()];
            content.readBytes(msgBody);

            GeneratedMessageV3 cmd = null;
            switch (msgCode) {
                case GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE:
                    cmd = GameMsgProtocol.UserEntryCmd.parseFrom(msgBody);
                    break;
                case GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE:
                    cmd = GameMsgProtocol.WhoElseIsHereCmd.parseFrom(msgBody);
                    break;
                case GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE:
                    cmd = GameMsgProtocol.UserMoveToCmd.parseFrom(msgBody);
                    break;
                default:
                    break;
            }
            //传递给下一个
            if (cmd != null) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
