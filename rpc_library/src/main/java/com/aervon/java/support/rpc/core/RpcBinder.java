package com.aervon.java.support.rpc.core;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcBinder.java </p>
 * <p> Description: RPC连接接口. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
public interface RpcBinder {

    RpcResponse transact(RpcRequest request) throws RemoteException;
}
