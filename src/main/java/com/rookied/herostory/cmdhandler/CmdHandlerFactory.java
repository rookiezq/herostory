package com.rookied.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.rookied.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rookied
 * @date 2022.03.10
 */
public class CmdHandlerFactory {

    private static Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> handlerMap = new HashMap<>();

    private CmdHandlerFactory() {
    }

    public static void init() {
        handlerMap.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
        handlerMap.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());
    }

    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (msgClazz == null) {
            return null;
        }

        return handlerMap.get(msgClazz);
    }
}
