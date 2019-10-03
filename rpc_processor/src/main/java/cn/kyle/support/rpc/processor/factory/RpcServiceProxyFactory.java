package cn.kyle.support.rpc.processor.factory;

import cn.kyle.support.rpc.annotation.RpcParam;
import cn.kyle.support.rpc.annotation.RpcReturnType;
import cn.kyle.support.rpc.annotation.RpcService;
import cn.kyle.support.rpc.core.RemoteException;
import cn.kyle.support.rpc.core.RpcBinder;
import cn.kyle.support.rpc.core.RpcRequest;
import cn.kyle.support.rpc.core.RpcResponse;
import cn.kyle.support.rpc.core.binder.RpcNettyBinder;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.TypeName;
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

import cn.kyle.support.rpc.processor.utils.CharsetExtension;
import cn.kyle.support.rpc.processor.utils.ClassNameExtension;

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
    private final static Class RPC_BINDER_CLASS = RpcNettyBinder.class;

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
            TypeKind returnTypeKind = methodElement.getReturnType().getKind();
            if (returnTypeKind.isPrimitive() || returnTypeKind == TypeKind.VOID) {
                methodSpecBuilder.returns(ClassNameExtension.bestGuessPrimaryType(returnTypeKind));
            } else {
                methodSpecBuilder.returns(ClassNameExtension.bestGuess(rpcReturnType));
            }
            // 处理参数
            List<? extends VariableElement> variableElements = methodElement.getParameters();
            for (VariableElement variableElement : variableElements) {
                RpcParam rpcParam = variableElement.getAnnotation(RpcParam.class);
                TypeKind typeKind = variableElement.asType().getKind();
                if (typeKind.isPrimitive()) {
                    methodSpecBuilder.addParameter(ClassNameExtension.bestGuessPrimaryType(typeKind), variableElement.getSimpleName().toString());
                } else {
                    methodSpecBuilder.addParameter(ClassNameExtension.bestGuess(rpcParam), variableElement.getSimpleName().toString());
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
     *     RpcRequest request = new RpcRequest("Service", "Method", params);
     *     RpcResponse response = rpcBinder.transact(request);
     *     if (response.getError() != null) {
     *       throw new RemoteException(response.getError());
     *     }
     *     return (String) response.getResult();
     * </p>
     * <p>
     *     占位符介绍： https://blog.csdn.net/IO_Field/article/details/89355941#S_1065
     * </p>
     */
    private CodeBlock generateTransactCode(ExecutableElement methodElement, List<? extends VariableElement> variableElements) {
        RpcService rpcService = mServiceClassElement.getAnnotation(RpcService.class);
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.add("$T<String, Object> params = new $T<>();\n", Map.class, HashMap.class);
        for (VariableElement element : variableElements) {
            codeBuilder.add("params.put($S, $L);\n", element.getSimpleName(), element.getSimpleName());
        }
        codeBuilder.add("$T rpcBinder = new $T($S, $L);\n", RpcBinder.class, RPC_BINDER_CLASS, rpcService.ip(), rpcService.port());
        codeBuilder.add("$T request = new $T($S, $S, params);\n", RpcRequest.class, RpcRequest.class, mServiceClassElement.getSimpleName(), methodElement.getSimpleName());
        codeBuilder.add("$T response = rpcBinder.transact(request);\n", RpcResponse.class);
        codeBuilder.add("if (response.hasError()) {\n");
        codeBuilder.add(CharsetExtension.CODE_INDENT + "throw new RemoteException(response.getError());\n");
        codeBuilder.add("}\n");
        if (methodElement.getReturnType().getKind() != TypeKind.VOID) {
            // 有返回值
            RpcReturnType rpcReturnType = methodElement.getAnnotation(RpcReturnType.class);
            TypeName returnType;
            TypeKind typeKind = methodElement.getReturnType().getKind();
            if (typeKind.isPrimitive()) {
                returnType = ClassNameExtension.bestGuessPrimaryType(typeKind);
            } else {
                returnType = ClassNameExtension.bestGuess(rpcReturnType);
            }
            codeBuilder.add("return ($T) response.getResult();\n", returnType);
        }
        return codeBuilder.build();
    }
}
