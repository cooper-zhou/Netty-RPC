package cn.kyle.support.nettyrpc.sample;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import cn.kyle.support.rpc.core.NettyRpc;
import cn.kyle.support.rpc.core.RemoteException;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: TestClient.java </p>
 * <p> Description: XXXXXXXXXX. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/10/2 </p>
 */
public class TestClient {

    public static void main(String args[]) throws IOException {
        UiAutomator uiAutomator = NettyRpc.create(UiAutomator.class);
        try {
            boolean result = uiAutomator.uiKeyClick(2, 3);
            System.out.println("result : " + result);

            String result2 = uiAutomator.getUserPhone("1234");
            System.out.println("result2 : " + result2);

            List<String> result3 = uiAutomator.getUserPhoneList(Arrays.asList("1234", "5678"));
            System.out.println("result3 : " + result3);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
