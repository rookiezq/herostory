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
public class UserMoveToCmdHandler implements ICmdHandler<GameMsgProtocol.UserMoveToCmd> {

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserMoveToCmd cmd) {
        //用户移动
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

}
