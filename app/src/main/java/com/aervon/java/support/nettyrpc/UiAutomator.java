package com.aervon.java.support.nettyrpc;

import com.aervon.java.support.rpc.annotation.RpcMethod;
import com.aervon.java.support.rpc.annotation.RpcParam;
import com.aervon.java.support.rpc.annotation.RpcReturnType;
import com.aervon.java.support.rpc.annotation.RpcService;
import com.aervon.java.support.rpc.core.RemoteException;

import java.util.List;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: UiAutomator.java </p>
 * <p> Description: XXXXXXXXXX. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
@RpcService(ip = "127.0.0.1", port = 8094)
public interface UiAutomator {

    @RpcMethod
    boolean uiKeyClick(@RpcParam int x, @RpcParam int y) throws RemoteException;

    @RpcMethod
    @RpcReturnType(type = String.class)
    String getUserPhone(@RpcParam(type = String.class) String userId) throws RemoteException;

    @RpcMethod
    @RpcReturnType(type = List.class, generic = String.class)
    List<String> getUserPhoneList(@RpcParam(type = List.class, generic = String.class) List<String> userIds) throws RemoteException;

    @RpcMethod
    void sleep() throws RemoteException;
}
