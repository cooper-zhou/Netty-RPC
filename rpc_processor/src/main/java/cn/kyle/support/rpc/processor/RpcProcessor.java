package cn.kyle.support.rpc.processor;

import cn.kyle.support.rpc.annotation.RpcMethod;
import cn.kyle.support.rpc.annotation.RpcParam;
import cn.kyle.support.rpc.annotation.RpcReturnType;
import cn.kyle.support.rpc.annotation.RpcService;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

import cn.kyle.support.rpc.processor.factory.ClassFactory;
import cn.kyle.support.rpc.processor.factory.RpcServiceFactory;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: RpcProcessor.java </p>
 * <p> Description: RPC框架注解处理器. </p>
 * <p> Author: Aervon </p>
 * <p> Date: 2019/9/8 </p>
 */
@AutoService(Processor.class)
public class RpcProcessor extends AbstractProcessor {

    private Map<String, ClassFactory> mClassFactoryMap = new HashMap<>();

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(RpcService.class.getCanonicalName());
        supportTypes.add(RpcMethod.class.getCanonicalName());
        supportTypes.add(RpcParam.class.getCanonicalName());
        supportTypes.add(RpcReturnType.class.getCanonicalName());
        return supportTypes;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        // 获取RpcService注解服务
        Set<? extends Element> rpcServices = roundEnvironment.getElementsAnnotatedWith(RpcService.class);
        for (Element element : rpcServices) {
            TypeElement classElement = (TypeElement) element;
            String fullClassName = classElement.getQualifiedName().toString();
            ClassFactory factory = mClassFactoryMap.get(fullClassName);
            if (factory == null) {
                factory = new RpcServiceFactory(processingEnv.getElementUtils().getPackageOf(classElement), classElement);
                mClassFactoryMap.put(fullClassName, factory);
            }
        }
        // 获取RpcMethod注解方法
        Set<? extends Element> rpcMethods = roundEnvironment.getElementsAnnotatedWith(RpcMethod.class);
        for (Element element : rpcMethods) {
            ExecutableElement executableElement = (ExecutableElement) element;
            String fullClassName = ((TypeElement) executableElement.getEnclosingElement()).getQualifiedName().toString();
            ClassFactory factory = mClassFactoryMap.get(fullClassName);
            if (factory != null) {
                factory.putMethodElement(executableElement);
            }
        }
        for (String key : mClassFactoryMap.keySet()) {
            ClassFactory factory = mClassFactoryMap.get(key);
            JavaFile javaFile = JavaFile.builder(factory.getPackageName(), factory.generateJavaCode()).build();
            try {
                javaFile.writeTo(processingEnv.getFiler());
                System.out.println("javaFile >>>>> " + javaFile.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mClassFactoryMap.clear();
        return true;
    }
}
