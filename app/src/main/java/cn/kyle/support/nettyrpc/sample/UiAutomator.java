package cn.kyle.support.nettyrpc.sample;

import java.util.List;

import cn.kyle.support.rpc.annotation.RpcMethod;
import cn.kyle.support.rpc.annotation.RpcParam;
import cn.kyle.support.rpc.annotation.RpcReturnType;
import cn.kyle.support.rpc.annotation.RpcService;
import cn.kyle.support.rpc.core.RemoteException;

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
