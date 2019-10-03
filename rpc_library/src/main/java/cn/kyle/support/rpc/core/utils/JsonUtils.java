package cn.kyle.support.rpc.core.utils;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.lang.reflect.Type;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: JsonUtils.java </p>
 * <p> Description: Json处理工具. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/28 </p>
 */
public class JsonUtils {

    private static final Gson GSON_INSTANCE = new Gson();

    public static <T> String toJson(T object) {
        return GSON_INSTANCE.toJson(object);
    }

    public static <T> T fromJson(String json, Type clazz) {
        try {
            return GSON_INSTANCE.fromJson(json, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getKeyAsString(String json, String key) {
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
        JsonElement jsonElement = jsonObject.get(key);
        if (jsonElement != null) {
            return jsonElement.toString();
        }
        return null;
    }
}
