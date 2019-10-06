package cn.kyle.support.nettyrpc.sample;

import java.io.IOException;

import cn.kyle.support.rpc.core.NettyService;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: TestServer.java </p>
 * <p> Description: XXXXXXXXXX. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/10/2 </p>
 */
public class TestServer {

    public static void main(String args[]) throws IOException {
        NettyService.getDefault().addService(new UiAutomatorImpl()).start(8094);
        while (true) {
            char read = (char) System.in.read();
            if (read == '0') {
                break;
            }
        }
    }
}
