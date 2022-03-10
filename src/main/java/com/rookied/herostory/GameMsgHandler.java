package com.rookied.herostory;

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
            if (msg instanceof GameMsgProtocol.UserEntryCmd) {
                //用户进场
                GameMsgProtocol.UserEntryCmd cmd = (GameMsgProtocol.UserEntryCmd) msg;
                int userId = cmd.getUserId();
                String heroAvatar = cmd.getHeroAvatar();

                UserManager.addUser(new User(userId, heroAvatar));
                //将id保存到session
                ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
                LOG.debug("userId:{} 进场", userId);
                GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
                builder.setUserId(userId);
                builder.setHeroAvatar(heroAvatar);

                // 构建结果并广播
                GameMsgProtocol.UserEntryResult userEntryResult = builder.build();
                Broadcaster.broadcast(userEntryResult);
            } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
                //还有谁在场
                GameMsgProtocol.WhoElseIsHereResult.Builder results = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

                for (User user : UserManager.listUser()) {
                    LOG.info("user:{}", user);
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder builder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                    builder.setUserId(user.getUserId());
                    builder.setHeroAvatar(user.getHeroAvatar());
                    results.addUserInfo(builder);
                }

                GameMsgProtocol.WhoElseIsHereResult result = results.build();
                ctx.writeAndFlush(result);
            } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
                //用户移动
                GameMsgProtocol.UserMoveToCmd cmd = (GameMsgProtocol.UserMoveToCmd) msg;

                GameMsgProtocol.UserMoveToResult.Builder builder = GameMsgProtocol.UserMoveToResult.newBuilder();
                Integer userId = (Integer) ctx.channel().attr(AttributeKey.valueOf("userId")).get();
                if (userId == null) {
                    return;
                }
                builder.setMoveUserId(userId);
                builder.setMoveToPosX(cmd.getMoveToPosX());
                builder.setMoveToPosY(cmd.getMoveToPosY());

                // 构建结果并广播
                GameMsgProtocol.UserMoveToResult userMoveToResult = builder.build();
                Broadcaster.broadcast(userMoveToResult);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }
}
