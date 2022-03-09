package com.rookied.herostory;

import com.rookied.herostory.msg.GameMsgProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
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
     * 信道组，使用static
     */
    public static ChannelGroup group = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    //public static Set<User> users = new HashSet<>(); //使用set必须对User重写equals和hashcode
    //所有用户
    public static Map<Integer, User> userMap = new HashMap<>();

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
            group.add(ctx.channel());
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

                userMap.put(userId, new User(userId, heroAvatar));

                GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
                builder.setUserId(userId);
                builder.setHeroAvatar(heroAvatar);

                // 构建结果并广播
                GameMsgProtocol.UserEntryResult userEntryResult = builder.build();
                group.writeAndFlush(userEntryResult);
            } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
                //还有谁在场
                GameMsgProtocol.WhoElseIsHereResult.Builder results = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

                for (User user : userMap.values()) {
                    //LOG.info("user:{}", user);
                    GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder builder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
                    builder.setUserId(user.getUserId());
                    builder.setHeroAvatar(user.getHeroAvatar());
                    results.addUserInfo(builder);
                }

                GameMsgProtocol.WhoElseIsHereResult result = results.build();
                ctx.writeAndFlush(result);
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

    }
}
