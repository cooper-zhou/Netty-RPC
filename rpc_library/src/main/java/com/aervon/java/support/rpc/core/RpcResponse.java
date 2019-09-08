package com.aervon.java.support.rpc.core;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcResponse.java </p>
 * <p> Description: RPC请求结果实体类. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
public class RpcResponse<T> {

    private String jsonrpc;
    private T result;
    private String error;
    private String id;

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean hasError() {
        return result == null && error != null;
    }
}
