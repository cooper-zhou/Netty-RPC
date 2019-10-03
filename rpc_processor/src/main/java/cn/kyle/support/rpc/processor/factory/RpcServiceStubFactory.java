package cn.kyle.support.rpc.processor.factory;

import cn.kyle.support.rpc.annotation.RpcParam;
import cn.kyle.support.rpc.core.RpcRequest;
import cn.kyle.support.rpc.core.RpcResponse;
import cn.kyle.support.rpc.core.binder.RpcNettyServiceHandler;
import cn.kyle.support.rpc.core.utils.ParamWrapper;
import com.google.gson.reflect.TypeToken;
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
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import cn.kyle.support.rpc.processor.utils.CharsetExtension;
import cn.kyle.support.rpc.processor.utils.ClassNameExtension;
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
     *       ParamWrapper paramWrapper = new ParamWrapper(request.getParams());
     *       String method = request.getMethod();
     *       RpcResponse response = new RpcResponse();
     *       Object result = null;
     *       try {
     *        if (method.equals("uiKeyClick")) {
     *          result = uiKeyClick((int) paramWrapper.get("x", int.class), (int) paramWrapper.get("y", int.class));
     *        }
     *       } catch (Exception e) {
     *         response.setError(e.toString());
     *       }
     *       response.setResult(result);
     *       sendResponse(ctx, response);
     *
     * 占位符介绍： https://blog.csdn.net/IO_Field/article/details/89355941#S_1065
     */
    private CodeBlock generateDispatchCode() {
        CodeBlock.Builder codeBuilder = CodeBlock.builder();
        codeBuilder.add("$T response = new $T();\n", RpcResponse.class, RpcResponse.class);
        codeBuilder.add("$T paramWrapper = new $T(request.getParams());\n", ParamWrapper.class, ParamWrapper.class);
        codeBuilder.add("String method = request.getMethod();\n");
        codeBuilder.add("Object result = null;\n");
        codeBuilder.add("try {\n");
        for (int methodIndex = 0; methodIndex < mServiceMethodElements.size(); methodIndex++) {
            ExecutableElement executableElement = mServiceMethodElements.get(methodIndex);
            if (methodIndex > 0) {
                codeBuilder.add(" else ");
            } else {
                codeBuilder.add(CharsetExtension.CODE_INDENT);
            }
            // 方法按照method处理
            codeBuilder.add("if (method.equals($S)) {\n", executableElement.getSimpleName().toString());
            // 处理返回值result接收
            if (executableElement.getReturnType().getKind() != TypeKind.VOID) {
                codeBuilder.add(CharsetExtension.CODE_INDENT_TWICE + "result = $L(", executableElement.getSimpleName().toString());
            } else {
                codeBuilder.add(CharsetExtension.CODE_INDENT_TWICE + "$L(", executableElement.getSimpleName().toString());
            }
            // 处理方法参数
            List<? extends VariableElement> parameters = executableElement.getParameters();
            for (int paramIndex = 0; paramIndex < parameters.size(); paramIndex++) {
                VariableElement variableElement = parameters.get(paramIndex);
                if (paramIndex > 0) {
                    codeBuilder.add(", ");
                }
                combineCastType(codeBuilder, variableElement);
                codeBuilder.add("paramWrapper.get($S, ", variableElement.getSimpleName().toString());
                combineParameterType(codeBuilder, variableElement);
                codeBuilder.add(")");
            }
            codeBuilder.add(");\n");
            codeBuilder.add(CharsetExtension.CODE_INDENT + "}");
            if (methodIndex == mServiceMethodElements.size() - 1) {
                // 最后一个if-else后需要换行
                codeBuilder.add("\n");
            }
        }
        codeBuilder.add("} catch (Exception e) {\n");
        codeBuilder.add(CharsetExtension.CODE_INDENT + "response.setError(e.toString());\n");
        codeBuilder.add("}\n");
        codeBuilder.add("response.setResult(result);\n");
        codeBuilder.add("sendResponse(ctx, response);\n");
        return codeBuilder.build();
    }

    /**
     * 结合强转类型
     */
    private void combineCastType(CodeBlock.Builder codeBuilder, VariableElement variableElement) {
        RpcParam rpcParam = variableElement.getAnnotation(RpcParam.class);
        TypeKind typeKind = variableElement.asType().getKind();
        if (typeKind.isPrimitive()) {
            codeBuilder.add("($T) ", ClassNameExtension.bestGuessPrimaryType(typeKind));
        } else {
            ClassName className = ClassNameExtension.bestGuess(rpcParam);
            ClassName genericName = ClassNameExtension.bestGuessGeneric(rpcParam);
            if (genericName.equals(ClassName.OBJECT)) {
                codeBuilder.add("($T) ", className);
            } else {
                codeBuilder.add("($T<$T>) ", className, genericName);
            }
        }
    }

    /**
     * 结合参数类型
     */
    private void combineParameterType(CodeBlock.Builder codeBuilder, VariableElement variableElement) {
        RpcParam rpcParam = variableElement.getAnnotation(RpcParam.class);
        TypeKind typeKind = variableElement.asType().getKind();
        if (typeKind.isPrimitive()) {
            codeBuilder.add("$T.class", ClassNameExtension.bestGuessPrimaryType(typeKind));
        } else {
            ClassName className = ClassNameExtension.bestGuess(rpcParam);
            ClassName genericName = ClassNameExtension.bestGuessGeneric(rpcParam);
            if (genericName.equals(ClassName.OBJECT)) {
                codeBuilder.add("$T.class", className);
            } else {
                codeBuilder.add("new $T<$T<$T>>(){}.getType()", TypeToken.class, className, genericName);
            }
        }
    }
}
