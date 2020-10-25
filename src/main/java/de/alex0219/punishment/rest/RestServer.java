package de.alex0219.punishment.rest;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * Created by Alexander on 19.08.2020 04:35
 * © 2020 Alexander Fiedler
 */
public class RestServer  {
    private final int PORT;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;
    ServerBootstrap bootstrap;

    public RestServer(int port) {
        PORT = port;
    }

    public void start() {

        bossGroup = new NioEventLoopGroup(2);
        workerGroup = new NioEventLoopGroup(2);

        bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();
                        p
                                .addLast(new HttpRequestDecoder())
                                .addLast(new HttpResponseEncoder())
                                .addLast(new RestWebHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .bind("127.0.0.1",PORT);

    }

    public void restart() {
        bootstrap.group().shutdownGracefully();
        start();
    }




}
