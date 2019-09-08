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
 * <p> File: RpcMethod.java </p>
 * <p> Description: RPC服务方法注解. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Inherited
@Documented
public @interface RpcMethod {

    String value();
}
