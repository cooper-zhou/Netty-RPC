package com.aervon.java.support.rpc.processor.factory;

import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcServiceProxyFactory.java </p>
 * <p> Description: RpcService生成类的内部类Stub. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
public class RpcServiceStubFactory extends RpcBaseFactory {

    private final static String INNER_CLASS_NAME = "Stub";

    RpcServiceStubFactory(PackageElement packageElement, TypeElement typeElement) {
        super(packageElement, typeElement);
    }
    
    @Override
    public TypeSpec generateJavaCode() {
        return TypeSpec.classBuilder(INNER_CLASS_NAME)
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .build();
    }
}
