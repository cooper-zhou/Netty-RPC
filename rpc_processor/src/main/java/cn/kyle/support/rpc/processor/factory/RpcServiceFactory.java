package cn.kyle.support.rpc.processor.factory;

import cn.kyle.support.rpc.annotation.RpcService;
import com.squareup.javapoet.TypeSpec;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcServiceFactory.java </p>
 * <p> Description: 对应注解 {@link RpcService} 的类生成器. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/9/8 </p>
 */
public class RpcServiceFactory extends RpcBaseFactory {

    private String mGenerateSimpleName;
    private ClassFactory mStubClassFactory;
    private ClassFactory mProxyClassFactory;

    public RpcServiceFactory(PackageElement packageElement, TypeElement typeElement) {
        super(packageElement, typeElement);
        mGenerateSimpleName = typeElement.getSimpleName().toString() + "Rpc";
        mStubClassFactory = new RpcServiceStubFactory(packageElement, typeElement);
        mProxyClassFactory = new RpcServiceProxyFactory(packageElement, typeElement);
    }

    @Override
    public boolean putMethodElement(ExecutableElement executableElement) {
        mStubClassFactory.putMethodElement(executableElement);
        mProxyClassFactory.putMethodElement(executableElement);
        return super.putMethodElement(executableElement);
    }

    @Override
    public TypeSpec generateJavaCode() {
        return TypeSpec.interfaceBuilder(mGenerateSimpleName)
                .addModifiers(Modifier.PUBLIC)
                .addType(mStubClassFactory.generateJavaCode())
                .addType(mProxyClassFactory.generateJavaCode())
                .build();
    }
}
