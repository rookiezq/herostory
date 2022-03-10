package com.rookied.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.rookied.herostory.Broadcaster;
import com.rookied.herostory.User;
import com.rookied.herostory.UserManager;
import com.rookied.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

/**
 * @author rookied
 * @date 2022.03.10
 */
public class UserEntryCmdHandler implements ICmdHandler<GameMsgProtocol.UserEntryCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserEntryCmd cmd) {
        //用户进场
        int userId = cmd.getUserId();
        String heroAvatar = cmd.getHeroAvatar();

        UserManager.addUser(new User(userId, heroAvatar));
        //将id保存到session
        ctx.channel().attr(AttributeKey.valueOf("userId")).set(userId);
        //LOG.debug("userId:{} 进场", userId);
        GameMsgProtocol.UserEntryResult.Builder builder = GameMsgProtocol.UserEntryResult.newBuilder();
        builder.setUserId(userId);
        builder.setHeroAvatar(heroAvatar);

        // 构建结果并广播
        GameMsgProtocol.UserEntryResult userEntryResult = builder.build();
        Broadcaster.broadcast(userEntryResult);
    }

}
