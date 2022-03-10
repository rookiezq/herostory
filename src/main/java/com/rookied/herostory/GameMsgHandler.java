package com.rookied.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.rookied.herostory.cmdhandler.*;
import com.rookied.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.AttributeKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author rookied
 * @date 2022.03.05
 */
public class GameMsgHandler extends SimpleChannelInboundHandler<Object> {
    private static final Logger LOG = LoggerFactory.getLogger(GameMsgHandler.class);

    /**
     * 加入信道组
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        if (ctx == null) {
            return;
        }
        try {
            super.channelActive(ctx);
            Broadcaster.addChannel(ctx.channel());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    /**
     * 离场
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        if (ctx == null) {
            return;
        }
        try {
            super.handlerRemoved(ctx);
            Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
            //注意判空
            if (userId == null) {
                return;
            }

            UserManager.removeUser(userId);
            //从信道组移除
            Broadcaster.removeChannel(ctx.channel());
            LOG.debug("userId:{} 离场", userId);

            GameMsgProtocol.UserQuitResult.Builder builder = GameMsgProtocol.UserQuitResult.newBuilder();
            builder.setQuitUserId(userId);
            GameMsgProtocol.UserQuitResult result = builder.build();
            //广播离场
            Broadcaster.broadcast(result);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) {
        if (null == ctx || null == msg) return;
        LOG.info("收到客户端消息, msgClazz = {}, msgBody = {}", msg.getClass().getSimpleName(), msg);

        try {
            ICmdHandler<? extends GeneratedMessageV3> handler = new CmdHandlerFactory().create(msg);
            handler.handle(ctx, cast(msg));

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }

    /**
     * 欺骗编译器
     */
    public <TCmd extends GeneratedMessageV3> TCmd cast(Object msg) {
        if (msg == null) {
            return null;
        }
        return (TCmd) msg;
    }
}
