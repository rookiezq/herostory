package com.rookied.herostory;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.rookied.herostory.msg.GameMsgProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author rookied
 * @date 2022.03.10
 */
public class GameMsgRecoginzer {
    private static final Logger LOG = LoggerFactory.getLogger(GameMsgRecoginzer.class);
    /**
     * 消息编号 -> 消息对象字典
     */
    private static final Map<Integer, GeneratedMessageV3> msgCodeAndMsgObjMap = new HashMap<>();
    /**
     * 消息类 -> 消息编号字典
     */
    private static final Map<Class<?>, Integer> clazzAndMsgCodeMap = new HashMap<>();

    public static void init() {
        GameMsgProtocol.MsgCode[] values = GameMsgProtocol.MsgCode.values();
        //将枚举cmd名字和对应的int提前放入map
        Map<String, Integer> msgCodeEnumMap = new HashMap<>(values.length - 1);
        for (GameMsgProtocol.MsgCode msgCode : values) {
            if (GameMsgProtocol.MsgCode.UNRECOGNIZED == msgCode) continue;
            String name = msgCode.name().replace("_", "");
            msgCodeEnumMap.put(name, msgCode.getNumber());
        }
        //遍历GameMsgProtocol内部类
        Class<?>[] classes = GameMsgProtocol.class.getClasses();
        for (Class<?> aClass : classes) {
            //去除不是GeneratedMessageV3的子类
            if (!GeneratedMessageV3.class.isAssignableFrom(aClass)) {
                continue;
            }

            String simpleName = aClass.getSimpleName().toUpperCase();
            Integer msgCode = msgCodeEnumMap.get(simpleName);

            try {
                Method method = aClass.getDeclaredMethod("getDefaultInstance");
                Object instance = method.invoke(aClass);
                msgCodeAndMsgObjMap.put(msgCode, (GeneratedMessageV3) instance);
                clazzAndMsgCodeMap.put(aClass, msgCode);
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        LOG.info("===完成命令与处理器的关联===");
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

    public static void main(String[] args) {
        GameMsgRecoginzer.init();
    }
}
