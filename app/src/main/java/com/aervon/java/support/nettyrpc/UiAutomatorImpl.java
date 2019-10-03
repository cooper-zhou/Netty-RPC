package com.aervon.java.support.nettyrpc;

import android.util.Log;

import com.aervon.java.support.rpc.core.RemoteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: UiAutomatorImpl.java </p>
 * <p> Description: XXXXXXXXXX. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/10/2 </p>
 */
public class UiAutomatorImpl extends UiAutomatorRpc.Stub {

    @Override
    public boolean uiKeyClick(int x, int y) throws RemoteException {
        Log.d("UiAutomatorImpl","uiKeyClick");
        return true;
    }

    @Override
    public String getUserPhone(String userId) throws RemoteException {
        Log.d("UiAutomatorImpl","getUserPhone");
        return "Kyle";
    }

    @Override
    public List<String> getUserPhoneList(List<String> userIds) throws RemoteException {
        Log.d("UiAutomatorImpl","getUserPhoneList");
        return Arrays.asList("Kyle");
    }

    @Override
    public void sleep() throws RemoteException {
        Log.d("UiAutomatorImpl","sleep");
    }
}
