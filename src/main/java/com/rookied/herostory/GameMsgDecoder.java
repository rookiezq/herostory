package com.rookied.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
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
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
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

            //获取消息构建器
            Message.Builder msgBuilder = GameMsgRecoginzer.getBuilderByMsgCode(msgCode);
            if (msgBuilder == null) {
                return;
            }
            msgBuilder.clear();
            msgBuilder.mergeFrom(msgBody);
            //构建消息实体
            Message cmd = msgBuilder.build();

            //传递给下一个
            if (cmd != null) {
                ctx.fireChannelRead(cmd);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
