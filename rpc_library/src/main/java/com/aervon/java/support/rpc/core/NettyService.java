package com.aervon.java.support.rpc.core;

import com.aervon.java.support.rpc.core.binder.RpcNettyServiceHandler;

import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: NettyService.java </p>
 * <p> Description: Netty服务端启动. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/10/3 </p>
 */
public class NettyService {

    private List<RpcNettyServiceHandler> mServiceHandlers;

    private NettyService(List<RpcNettyServiceHandler> handlers) {
        mServiceHandlers = handlers;
    }

    public static NettyServiceBuilder getDefault() {
        return new NettyServiceBuilder();
    }

    private void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            for (RpcNettyServiceHandler serviceHandler : mServiceHandlers) {
                                ch.pipeline().addLast(serviceHandler);
                            }
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    public static class NettyServiceBuilder {

        private List<RpcNettyServiceHandler> handlers;

        public NettyServiceBuilder addService(RpcNettyServiceHandler handler) {
            if (!handlers.contains(handler)) {
                handlers.add(handler);
            }
            return this;
        }

        public void start(int port) {
            new NettyService(handlers).start(port);
        }
    }
}
