package com.aervon.java.support.rpc.core.binder;

import com.aervon.java.support.rpc.core.RemoteException;
import com.aervon.java.support.rpc.core.RpcBinder;
import com.aervon.java.support.rpc.core.RpcRequest;
import com.aervon.java.support.rpc.core.RpcResponse;
import com.aervon.java.support.rpc.core.Constants;
import com.aervon.java.support.rpc.core.utils.JsonUtils;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMessage;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestEncoder;
import io.netty.handler.codec.http.HttpResponseDecoder;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

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
        final Bootstrap bootstrap = getTransactBootstrap(new RpcNettyBinderHandler() {

            @Override
            public void channelActive(ChannelHandlerContext ctx) throws Exception {
                super.channelActive(ctx);
                sendRequest(ctx, request);
            }

            @Override
            protected void channelReceived(ChannelHandlerContext ctx, RpcResponse rpcResponse) {
                synchronized (request) {
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

    private Bootstrap getTransactBootstrap(final RpcNettyBinderHandler channelHandler) {
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(mWorkGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<Channel>() {
                    @Override
                    protected void initChannel(Channel ch) {
                        ch.pipeline().addLast(new HttpRequestEncoder());
                        ch.pipeline().addLast(new HttpResponseDecoder());
                        ch.pipeline().addLast(new HttpObjectAggregator(65536));
                        ch.pipeline().addLast(channelHandler);
                    }
                });
        return bootstrap;
    }

    private abstract class RpcNettyBinderHandler extends SimpleChannelInboundHandler<HttpMessage> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, HttpMessage msg) {
            if (msg instanceof FullHttpResponse) {
                RpcResponse rpcResponse = getResponseBody((FullHttpResponse) msg);
                channelReceived(ctx, rpcResponse);
            }
        }

        protected abstract void channelReceived(ChannelHandlerContext ctx, RpcResponse rpcResponse);

        private RpcResponse getResponseBody(FullHttpResponse httpResponse) {
            ByteBuf byteBuf = httpResponse.content();
            String body = byteBuf.toString(CharsetUtil.UTF_8);
            return JsonUtils.fromJson(body, RpcResponse.class);
        }

        /**
         * 将RPC请求转换成HTTP请求，并发送
         */
        void sendRequest(ChannelHandlerContext ctx, RpcRequest rpcRequest) {
            rpcRequest.setJsonrpc(Constants.JSON_RPC_VERSION);
            try {
                URI url = new URI(String.format("%s:%s", mTargetIp, mTargetPort));
                String msg = JsonUtils.toJson(rpcRequest);
                FullHttpRequest request = new DefaultFullHttpRequest(
                        HttpVersion.HTTP_1_0, HttpMethod.POST, url.toASCIIString(), Unpooled.wrappedBuffer(msg.getBytes(CharsetUtil.UTF_8)));
                request.headers()
                        .set(HttpHeaders.Names.CONTENT_TYPE, "text/plain;charset=UTF-8")
                        .set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
                        .set(HttpHeaders.Names.CONTENT_LENGTH, request.content().readableBytes());
                ctx.writeAndFlush(request);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}
