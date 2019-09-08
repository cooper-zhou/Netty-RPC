package com.aervon.java.support.rpc.core;

import java.util.Map;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcRequest.java </p>
 * <p> Description: RPC请求实体类. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
public class RpcRequest {

    private String jsonrpc;
    private String method;
    private Map<String, Object> params;
    private String id;

    public RpcRequest(String method, Map<String, Object> params) {
        this.method = method;
        this.params = params;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public void setJsonrpc(String jsonrpc) {
        this.jsonrpc = jsonrpc;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
