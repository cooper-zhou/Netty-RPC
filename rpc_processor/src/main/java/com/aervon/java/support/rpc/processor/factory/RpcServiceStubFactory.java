package com.aervon.java.support.rpc.processor.factory;

import com.aervon.java.support.rpc.core.RpcRequest;
import com.aervon.java.support.rpc.core.binder.RpcNettyBinder;
import com.aervon.java.support.rpc.core.binder.RpcNettyServiceHandler;
import com.aervon.java.support.rpc.core.utils.ParamWrapper;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

import io.netty.channel.ChannelHandlerContext;

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
                .superclass(RpcNettyServiceHandler.class)
                .addSuperinterface(ClassName.bestGuess(mServiceClassName))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.ABSTRACT)
                .addMethods(generateMethodCode())
                .build();
    }

    private List<MethodSpec> generateMethodCode() {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        MethodSpec channelReceivedMethod = MethodSpec.methodBuilder("channelReceived")
                .addModifiers(Modifier.PROTECTED)
                .returns(TypeName.VOID)
                .addParameter(ChannelHandlerContext.class, "ctx")
                .addParameter(RpcRequest.class, "request")
                .addCode(generateDispatchCode())
                .build();
        methodSpecs.add(channelReceivedMethod);
        return methodSpecs;
    }

    /**
     * 占位符介绍： https://blog.csdn.net/IO_Field/article/details/89355941#S_1065
     */
    private CodeBlock generateDispatchCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.add("$T paramWrapper = new $T(request.getParams());\n", ParamWrapper.class, ParamWrapper.class);
        codeBuilder.add("String method = request.getMethod();\n");
//        for (int i = 0; i < mServiceMethodElements.size(); i++) {
//            if (i > 0) {
//                codeBuilder.add("else");
//            }
//            codeBuilder.add(" if ()")
//        }
        return codeBuilder.build();
    }
}
