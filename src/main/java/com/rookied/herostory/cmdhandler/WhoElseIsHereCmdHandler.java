package com.rookied.herostory.cmdhandler;

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
public class WhoElseIsHereCmdHandler implements ICmdHandler<GameMsgProtocol.WhoElseIsHereCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.WhoElseIsHereCmd cmd) {
        //还有谁在场
        GameMsgProtocol.WhoElseIsHereResult.Builder results = GameMsgProtocol.WhoElseIsHereResult.newBuilder();

        for (User user : UserManager.listUser()) {
            //LOG.info("user:{}", user);
            GameMsgProtocol.WhoElseIsHereResult.UserInfo.Builder builder = GameMsgProtocol.WhoElseIsHereResult.UserInfo.newBuilder();
            builder.setUserId(user.getUserId());
            builder.setHeroAvatar(user.getHeroAvatar());
            results.addUserInfo(builder);
        }

        GameMsgProtocol.WhoElseIsHereResult result = results.build();
        ctx.writeAndFlush(result);
    }

}
