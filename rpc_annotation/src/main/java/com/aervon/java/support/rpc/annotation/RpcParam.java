package com.aervon.java.support.rpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcParam.java </p>
 * <p> Description: XXXXXXXXXX. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.CLASS)
@Inherited
@Documented
public @interface RpcParam {

    /**
     * 如果参数类型不是基础类型，需要传入参数类型的全类名
     */
    String type() default "";
}
