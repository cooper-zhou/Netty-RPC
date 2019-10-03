package cn.kyle.support.rpc.core;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RemoteException.java </p>
 * <p> Description: 远程服务调用异常. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
public class RemoteException extends Exception {

    public RemoteException() {}

    public RemoteException(String msg) {
        super(msg);
    }

    public RemoteException(Throwable throwable) {
        super(throwable);
    }
}
