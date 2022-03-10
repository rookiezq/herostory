package com.rookied.herostory.cmdhandler;

import com.google.protobuf.GeneratedMessageV3;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author rookied
 * @date 2022.03.10
 */
public interface ICmdHandler<Tcmd extends GeneratedMessageV3> {

    void handle(ChannelHandlerContext ctx, Tcmd cmd);
}
