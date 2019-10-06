package cn.kyle.support.rpc.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcReturnType.java </p>
 * <p> Description: XXXXXXXXXX. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/9/8 </p>
 */

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.CLASS)
@Inherited
@Documented
public @interface RpcReturnType {

    /**
     * 如果返回类型不是基础类型，需要传入返回类型的全类名
     */
    Class<?> type() default Object.class;

    Class<?> generic() default Object.class;
}
