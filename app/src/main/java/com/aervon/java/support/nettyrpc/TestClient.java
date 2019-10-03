package com.aervon.java.support.nettyrpc;

import com.aervon.java.support.rpc.core.NettyRpc;
import com.aervon.java.support.rpc.core.RemoteException;

import java.io.IOException;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: TestClient.java </p>
 * <p> Description: XXXXXXXXXX. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/10/2 </p>
 */
public class TestClient {

    public static void main(String args[]) throws IOException {
        UiAutomator uiAutomator = NettyRpc.create(UiAutomator.class);
        try {
            uiAutomator.uiKeyClick(2, 3);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
