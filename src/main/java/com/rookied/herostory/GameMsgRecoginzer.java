package com.rookied.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.rookied.herostory.msg.GameMsgProtocol;

import java.util.HashMap;
import java.util.Map;

/**
 * @author rookied
 * @date 2022.03.10
 */
public class GameMsgRecoginzer {
    /**
     * 消息编号 -> 消息对象字典
     */
    private static final Map<Integer, GeneratedMessageV3> msgCodeAndMsgObjMap = new HashMap<>();
    /**
     * 消息类 -> 消息编号字典
     */
    private static final Map<Class<?>, Integer> clazzAndMsgCodeMap = new HashMap<>();

    public static void init() {
        msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_ENTRY_CMD_VALUE, GameMsgProtocol.UserEntryCmd.getDefaultInstance());
        msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_CMD_VALUE, GameMsgProtocol.WhoElseIsHereCmd.getDefaultInstance());
        msgCodeAndMsgObjMap.put(GameMsgProtocol.MsgCode.USER_MOVE_TO_CMD_VALUE, GameMsgProtocol.UserMoveToCmd.getDefaultInstance());

        clazzAndMsgCodeMap.put(GameMsgProtocol.UserEntryResult.class, GameMsgProtocol.MsgCode.USER_ENTRY_RESULT_VALUE);
        clazzAndMsgCodeMap.put(GameMsgProtocol.WhoElseIsHereResult.class, GameMsgProtocol.MsgCode.WHO_ELSE_IS_HERE_RESULT_VALUE);
        clazzAndMsgCodeMap.put(GameMsgProtocol.UserMoveToResult.class, GameMsgProtocol.MsgCode.USER_MOVE_TO_RESULT_VALUE);
        clazzAndMsgCodeMap.put(GameMsgProtocol.UserQuitResult.class, GameMsgProtocol.MsgCode.USER_QUIT_RESULT_VALUE);
    }

    private GameMsgRecoginzer() {
    }

    /**
     * 根据消息编号获取消息构建器
     */
    public static Message.Builder getBuilderByMsgCode(int msgCode) {
        if (msgCode < -1) {
            return null;
        }
        GeneratedMessageV3 defaultMsg = msgCodeAndMsgObjMap.get(msgCode);
        if (null == defaultMsg) {
            return null;
        }
        return defaultMsg.newBuilderForType();

    }

    /**
     * 根据消息类获取消息编号
     */
    public static int getMsgCodeByClazz(Class<?> msgClazz) {
        if (msgClazz == null) {
            return -1;
        }
        return clazzAndMsgCodeMap.get(msgClazz);
    }
}
