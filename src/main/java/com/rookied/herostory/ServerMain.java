package com.rookied.herostory;

import com.rookied.herostory.cmdhandler.CmdHandlerFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author rookied
 * @date 2022.03.04
 */
public class ServerMain {

    static private final Logger LOG = LoggerFactory.getLogger(ServerMain.class);

    static private final int SERVER_PORT = 12345;

    public static void main(String[] args) {
        CmdHandlerFactory.init();

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup);
        b.channel(NioServerSocketChannel.class); //服务器信道的处理方式
        b.childHandler(new ChannelInitializer<SocketChannel>() { //客户端信道的处理方式
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(
                        new HttpServerCodec(), //http 服务器编解码
                        new HttpObjectAggregator(65535), //内容长度限制
                        new WebSocketServerProtocolHandler("/websocket"), //websocket协议处理器，在这里握手、ping、pong等消息
                        new GameMsgDecoder(), //自定义解码器
                        new GameMsgEncoder(), //自定义编码器
                        new GameMsgHandler() //自定义消息处理器
                );
            }
        });
        try {
            ChannelFuture f = b.bind(SERVER_PORT).sync();
            if (f.isSuccess()) {
                LOG.info("服务器启动成功");
            }
            //等待服务器信道关闭，可以一直等待客户端服务
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}
