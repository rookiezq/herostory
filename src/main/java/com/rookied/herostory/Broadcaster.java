package com.rookied.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * @author rookied
 * @date 2022.03.10
 */
public final class Broadcaster {

    /**
     * 信道组，使用static
     */
    private static final ChannelGroup GROUP = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private Broadcaster() {
    }

    /**
     * 增加信道
     */
    public static void addChannel(Channel channel) {
        if (channel != null) {
            GROUP.add(channel);
        }
    }

    /**
     * 移除信道
     */
    public static void removeChannel(Channel channel) {
        if (channel != null) {
            GROUP.remove(channel);
        }
    }

    /**
     * 广播
     */
    public static void broadcast(Object msg) {
        if (msg != null) {
            GROUP.writeAndFlush(msg);
        }
    }

}
