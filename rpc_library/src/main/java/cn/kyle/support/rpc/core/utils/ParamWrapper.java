package cn.kyle.support.rpc.core.utils;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: ParamWrapper.java </p>
 * <p> Description: 参数解析类. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/9/28 </p>
 */
public class ParamWrapper {

    private String paramsJson;

    public ParamWrapper(Map<String, Object> params) {
        this.paramsJson = JsonUtils.toJson(params);
    }

    public <T> T get(String key, Type type) {
        String keyObject = JsonUtils.getKeyAsString(paramsJson, key);
        return JsonUtils.fromJson(keyObject, type);
    }
}
