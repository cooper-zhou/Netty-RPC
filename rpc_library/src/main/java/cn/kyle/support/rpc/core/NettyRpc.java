package cn.kyle.support.rpc.core;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: NettyRpc.java </p>
 * <p> Description: Netty RPC服务调用者. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/10/3 </p>
 */
public class NettyRpc {

    public static <T> T create(Class<T> clazz) {
        String rpcServiceName = clazz.getCanonicalName() + "Rpc$Proxy";
        try {
            Class<?> rpcProxyClass = Class.forName(rpcServiceName);
            return (T) rpcProxyClass.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }
}
