package cn.kyle.support.rpc.processor.factory;

import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcBaseFactory.java </p>
 * <p> Description: RPC相关类生成器基础类. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/9/8 </p>
 */
public abstract class RpcBaseFactory implements ClassFactory {

    private String mPackageName;
    String mServiceClassName;
    TypeElement mServiceClassElement;

    List<ExecutableElement> mServiceMethodElements = new ArrayList<>();

    RpcBaseFactory(PackageElement packageElement, TypeElement typeElement) {
        mPackageName = packageElement.getQualifiedName().toString();
        mServiceClassElement = typeElement;
        mServiceClassName = typeElement.getQualifiedName().toString();
    }

    @Override
    public boolean putMethodElement(ExecutableElement executableElement) {
        return mServiceMethodElements.add(executableElement);
    }

    @Override
    public String getPackageName() {
        return mPackageName;
    }
}
