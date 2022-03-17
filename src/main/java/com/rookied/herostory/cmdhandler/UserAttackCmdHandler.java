package com.rookied.herostory.cmdhandler;

import com.rookied.herostory.msg.GameMsgProtocol;
import io.netty.channel.ChannelHandlerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rookied
 * @date 2022.03.17
 */
public class UserAttackCmdHandler implements ICmdHandler<GameMsgProtocol.UserAttkCmd> {
    private static final Logger LOG = LoggerFactory.getLogger(UserAttackCmdHandler.class);

    @Override
    public void handle(ChannelHandlerContext ctx, GameMsgProtocol.UserAttkCmd cmd) {
        LOG.info("攻击了");
    }
}
