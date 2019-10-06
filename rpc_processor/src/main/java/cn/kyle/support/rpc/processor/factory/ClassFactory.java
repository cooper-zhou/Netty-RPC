package cn.kyle.support.rpc.processor.factory;

import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.ExecutableElement;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: ClassFactory.java </p>
 * <p> Description: 类生成器. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/9/8 </p>
 */
public interface ClassFactory {

    String getPackageName();

    boolean putMethodElement(ExecutableElement executableElement);

    TypeSpec generateJavaCode();
}
