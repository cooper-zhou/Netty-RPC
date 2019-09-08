package com.aervon.java.support.rpc.processor.factory;

import com.aervon.java.support.rpc.annotation.RpcMethod;
import com.aervon.java.support.rpc.annotation.RpcParam;
import com.aervon.java.support.rpc.annotation.RpcReturnType;
import com.aervon.java.support.rpc.annotation.RpcService;
import com.aervon.java.support.rpc.core.RemoteException;
import com.aervon.java.support.rpc.core.RpcBinder;
import com.aervon.java.support.rpc.core.RpcRequest;
import com.aervon.java.support.rpc.core.RpcResponse;
import com.aervon.java.support.rpc.core.binder.RpcNettyBinder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeSpec;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcServiceProxyFactory.java </p>
 * <p> Description: RpcService生成类的内部类Proxy. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
public class RpcServiceProxyFactory extends RpcBaseFactory {

    private final static String INNER_CLASS_NAME = "Proxy";
    private final static String RPC_BINDER_CLASS = RpcNettyBinder.class.getCanonicalName();

    RpcServiceProxyFactory(PackageElement packageElement, TypeElement typeElement) {
        super(packageElement, typeElement);
    }

    @Override
    public TypeSpec generateJavaCode() {
        return TypeSpec.classBuilder(INNER_CLASS_NAME)
                .addSuperinterface(ClassName.bestGuess(mServiceClassName))
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addMethods(generateMethodCode())
                .build();
    }

    private List<MethodSpec> generateMethodCode() {
        List<MethodSpec> methodSpecs = new ArrayList<>();
        for (ExecutableElement methodElement : mServiceMethodElements) {
            MethodSpec.Builder methodSpecBuilder = MethodSpec.methodBuilder(methodElement.getSimpleName().toString())
                    .addModifiers(Modifier.PUBLIC);
            // 处理返回值
            RpcReturnType rpcReturnType = methodElement.getAnnotation(RpcReturnType.class);
            if (rpcReturnType != null && !"".equals(rpcReturnType.type())) {
                methodSpecBuilder.returns(ClassName.bestGuess(rpcReturnType.type()));
            } else {
                methodSpecBuilder.returns(getPrimaryType(methodElement.getReturnType().getKind()));
            }
            // 处理参数
            List<? extends VariableElement> variableElements = methodElement.getParameters();
            for (VariableElement variableElement : variableElements) {
                RpcParam rpcParam = variableElement.getAnnotation(RpcParam.class);
                if (rpcParam != null && !"".equals(rpcParam.type())) {
                    methodSpecBuilder.addParameter(ClassName.bestGuess(rpcParam.type()), variableElement.getSimpleName().toString());
                } else {
                    methodSpecBuilder.addParameter(getPrimaryType(variableElement.asType().getKind()), variableElement.getSimpleName().toString());
                }
            }
            // 处理异常
            methodSpecBuilder.addException(RemoteException.class);
            // 处理代码
            methodSpecBuilder.addCode(generateTransactCode(methodElement, variableElements));
            methodSpecs.add(methodSpecBuilder.build());
        }
        return methodSpecs;
    }

    /**
     * 生成RPC请求核心代码
     * <p>
     *     Map<String, Object> params = new HashMap<>();
     *     params.put("x", x);
     *     RpcBinder rpcBinder = new RpcNettyBinder(ip, port);
     *     RpcResponse response = rpcBinder.transact(request);
     *     if (response.getError() != null) {
     *       throw new RemoteException(response.getError());
     *     }
     *     return (String) response.getResult();
     * </p>
     */
    private CodeBlock generateTransactCode(ExecutableElement methodElement, List<? extends VariableElement> variableElements) {
        RpcService rpcService = mServiceClassElement.getAnnotation(RpcService.class);
        RpcMethod rpcMethod = methodElement.getAnnotation(RpcMethod.class);
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.add(Map.class.getCanonicalName() + "<String, Object> params = new " + HashMap.class.getCanonicalName() + "<>();\n");
        for (VariableElement element : variableElements) {
            codeBuilder.add("params.put(\"" + element.getSimpleName() + "\", " + element.getSimpleName() + ");\n");
        }
        codeBuilder.add(RpcBinder.class.getCanonicalName() + " rpcBinder = new " + RPC_BINDER_CLASS + "(\"" + rpcService.ip() +"\", " + rpcService.port() + ");\n");
        codeBuilder.add(RpcRequest.class.getCanonicalName() + " request = new " + RpcRequest.class.getCanonicalName() + "(\"" + rpcMethod.value() + "\", params);\n");
        codeBuilder.add(RpcResponse.class.getCanonicalName() + " response = rpcBinder.transact(request);\n");
        codeBuilder.add("if (response.hasError()) {\n");
        codeBuilder.add("throw new RemoteException(response.getError());\n");
        codeBuilder.add("}\n");
        if (methodElement.getReturnType().getKind() != TypeKind.VOID) {
            // 有返回值
            RpcReturnType rpcReturnType = methodElement.getAnnotation(RpcReturnType.class);
            String returnType;
            if (rpcReturnType != null && !"".equals(rpcReturnType.type())) {
                returnType = rpcReturnType.type();
            } else {
                returnType = getPrimaryType(methodElement.getReturnType().getKind()).toString();
            }
            codeBuilder.add("return (" + returnType + ") response.getResult();\n");
        }
        return codeBuilder.build();
    }
}
