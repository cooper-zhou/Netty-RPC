package cn.kyle.support.nettyrpc.sample;

import java.util.Arrays;
import java.util.List;

import cn.kyle.support.rpc.core.RemoteException;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: UiAutomatorImpl.java </p>
 * <p> Description: XXXXXXXXXX. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/10/2 </p>
 */
public class UiAutomatorImpl extends UiAutomatorRpc.Stub {

    @Override
    public boolean uiKeyClick(int x, int y) throws RemoteException {
        System.out.println("UiAutomatorImpl execute uiKeyClick");
        return true;
    }

    @Override
    public String getUserPhone(String userId) throws RemoteException {
        System.out.println("UiAutomatorImpl execute getUserPhone");
        return "Kyle";
    }

    @Override
    public List<String> getUserPhoneList(List<String> userIds) throws RemoteException {
        System.out.println("UiAutomatorImpl execute getUserPhoneList");
        return Arrays.asList("Kyle");
    }

    @Override
    public void sleep() throws RemoteException {
        System.out.println("UiAutomatorImpl execute sleep");
    }
}
