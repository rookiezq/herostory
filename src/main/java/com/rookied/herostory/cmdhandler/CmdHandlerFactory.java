package com.rookied.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import com.rookied.herostory.GameMsgRecoginzer;
import com.rookied.herostory.msg.GameMsgProtocol;
import com.rookied.herostory.util.PackageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author rookied
 * @date 2022.03.10
 */
public class CmdHandlerFactory {
    private static final Logger LOG = LoggerFactory.getLogger(CmdHandlerFactory.class);

    private static Map<Class<?>, ICmdHandler<? extends GeneratedMessageV3>> handlerMap = new HashMap<>();

    private CmdHandlerFactory() {
    }

    public static void init() {
        /*handlerMap.put(GameMsgProtocol.UserEntryCmd.class, new UserEntryCmdHandler());
        handlerMap.put(GameMsgProtocol.WhoElseIsHereCmd.class, new WhoElseIsHereCmdHandler());
        handlerMap.put(GameMsgProtocol.UserMoveToCmd.class, new UserMoveToCmdHandler());*/
        String pkgName = ICmdHandler.class.getPackage().getName();
        Set<Class<?>> classes = PackageUtil.listSubClazz(pkgName, true, ICmdHandler.class);
        for (Class<?> subClass : classes) {
            //跳过抽象类和接口
            if (subClass == null
                    || 0 != (subClass.getModifiers() & Modifier.ABSTRACT)
                    || subClass.isInterface()) {
                continue;
            }
            Method[] methods = subClass.getDeclaredMethods();
            Class<?> cmdClass = null;
            for (Method method : methods) {
                //跳过非handle方法
                if (method == null || !"handle".equals(method.getName())) {
                    continue;
                }
                //参数类型
                Class<?>[] parameterTypes = method.getParameterTypes();
                //跳过其他重载方法
                if (parameterTypes.length < 2
                        || GeneratedMessageV3.class == parameterTypes[1]
                        || !GeneratedMessageV3.class.isAssignableFrom(parameterTypes[1])) {
                    continue;
                }
                cmdClass = parameterTypes[1];
                break;
            }
            if (cmdClass == null) {
                continue;
            }
            try {
                ICmdHandler<?> cmdHandler = (ICmdHandler<?>) subClass.newInstance();
                LOG.info(
                        "{} <==> {}",
                        cmdClass.getName(),
                        subClass.getName()
                );
                handlerMap.put(cmdClass, cmdHandler);
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public static ICmdHandler<? extends GeneratedMessageV3> create(Class<?> msgClazz) {
        if (msgClazz == null) {
            return null;
        }

        return handlerMap.get(msgClazz);
    }

    public static void main(String[] args) {
        init();
    }
}
