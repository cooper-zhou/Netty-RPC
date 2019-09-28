package com.aervon.java.support.rpc.core.utils;

import java.util.Map;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: ParamWrapper.java </p>
 * <p> Description: 参数解析类. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/28 </p>
 */
public class ParamWrapper {

    private String paramsJson;

    public ParamWrapper(Map<String, Object> params) {
        this.paramsJson = JsonUtils.toJson(params);
    }

    public <T> T get(String key, Class<T> clazz) {
        String keyObject = JsonUtils.getKeyAsString(paramsJson, key);
        return JsonUtils.fromJson(keyObject, clazz);
    }
}
