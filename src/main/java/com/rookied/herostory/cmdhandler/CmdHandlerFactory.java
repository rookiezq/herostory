package com.rookied.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.rookied.herostory.msg.GameMsgProtocol;

/**
 * @author rookied
 * @date 2022.03.10
 */
public class CmdHandlerFactory {

    public ICmdHandler<? extends GeneratedMessageV3> create(Object msg) {
        if (msg == null) {
            return null;
        }
        if (msg instanceof GameMsgProtocol.UserEntryCmd) {
            return new UserEntryCmdHandler();
        } else if (msg instanceof GameMsgProtocol.WhoElseIsHereCmd) {
            return new WhoElseIsHereCmdHandler();
        } else if (msg instanceof GameMsgProtocol.UserMoveToCmd) {
            return new UserMoveToCmdHandler();
        }
        return null;
    }
}
