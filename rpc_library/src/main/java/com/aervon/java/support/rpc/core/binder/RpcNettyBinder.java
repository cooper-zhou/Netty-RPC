package com.aervon.java.support.rpc.core.binder;

import com.aervon.java.support.rpc.core.RemoteException;
import com.aervon.java.support.rpc.core.RpcBinder;
import com.aervon.java.support.rpc.core.RpcRequest;
import com.aervon.java.support.rpc.core.RpcResponse;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcNettyBinder.java </p>
 * <p> Description: Netty实现RPC短连接. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
public class RpcNettyBinder implements RpcBinder {

    private String mTargetIp;
    private int mTargetPort;

    private NioEventLoopGroup mWorkGroup = new NioEventLoopGroup();

    private Map<RpcRequest, RpcResponse> mResponseMap = new HashMap<>();
    private Map<RpcRequest, RemoteException> mExceptionMap = new HashMap<>();

    public RpcNettyBinder(String ip, int port) {
        mTargetIp = ip;
        mTargetPort = port;
    }

    @Override
    public RpcResponse transact(RpcRequest request) throws RemoteException {
        try {
            return blockTransact(request);
        } catch (InterruptedException e) {
            throw new RemoteException(e);
        }
    }

    private RpcResponse blockTransact(final RpcRequest request) throws InterruptedException, RemoteException {
        final Bootstrap bootstrap = getTransactBootstrap(new SimpleChannelInboundHandler<String>() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
                String msg = new Gson().toJson(request);
                ctx.writeAndFlush(msg);
            }

            @Override
            protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
                synchronized (request) {
                    RpcResponse rpcResponse = new Gson().fromJson(msg, RpcResponse.class);
                    mResponseMap.put(request, rpcResponse);
                    request.notify();
                }
            }

            @Override
            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                super.exceptionCaught(ctx, cause);
                synchronized (request) {
                    mExceptionMap.put(request, new RemoteException(cause));
                    request.notify();
                }
            }
        });
        bootstrap.connect(mTargetIp, mTargetPort);
        synchronized (request) {
            request.wait();
        }
        if (!mResponseMap.containsKey(request) && mExceptionMap.containsKey(request)) {
            throw mExceptionMap.remove(request);
        }
        return mResponseMap.remove(request);
    }

    private Bootstrap getTransactBootstrap(final ChannelHandler channelHandler) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(mWorkGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(channelHandler);
                    }
                });
        return bootstrap;
    }
}
