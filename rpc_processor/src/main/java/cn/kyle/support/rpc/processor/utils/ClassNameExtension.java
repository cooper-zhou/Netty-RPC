package cn.kyle.support.rpc.processor.utils;

import cn.kyle.support.rpc.annotation.RpcParam;
import cn.kyle.support.rpc.annotation.RpcReturnType;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.TypeName;

import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.MirroredTypeException;
import javax.lang.model.type.TypeKind;

/**
 * <p> Project: NettyRpc </p>
 * <p> Version: 1.0 </p>
 * <p> File: ClassNameExtension.java </p>
 * <p> Description: ClassName拓展对象. </p>
 * <p> Author: kyle.zhou </p>
 * <p> Date: 2019/9/28 </p>
 */
public class ClassNameExtension {

    public static TypeName bestGuessPrimaryType(TypeKind kind) {
        switch (kind) {
            case BOOLEAN:
                return ClassName.BOOLEAN;
            case BYTE:
                return ClassName.BYTE;
            case SHORT:
                return ClassName.SHORT;
            case INT:
                return ClassName.INT;
            case LONG:
                return ClassName.LONG;
            case CHAR:
                return ClassName.CHAR;
            case FLOAT:
                return ClassName.FLOAT;
            case DOUBLE:
                return ClassName.DOUBLE;
            case VOID:
                return ClassName.VOID;
            default:
                return ClassName.OBJECT;
        }
    }

    public static ClassName bestGuess(RpcParam rpcParam) {
        if (rpcParam == null) {
            return ClassName.OBJECT;
        }
        String className;
        try {
            Class<?> type = rpcParam.type();
            className = type.getCanonicalName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            className = classTypeElement.getQualifiedName().toString();
        }
        return ClassName.bestGuess(className);
    }

    public static ClassName bestGuessGeneric(RpcParam rpcParam) {
        if (rpcParam == null) {
            return ClassName.OBJECT;
        }
        String className;
        try {
            Class<?> type = rpcParam.generic();
            className = type.getCanonicalName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            className = classTypeElement.getQualifiedName().toString();
        }
        return ClassName.bestGuess(className);
    }

    public static ClassName bestGuess(RpcReturnType rpcReturnType) {
        if (rpcReturnType == null) {
            return ClassName.OBJECT;
        }
        String className;
        try {
            Class<?> type = rpcReturnType.type();
            className = type.getCanonicalName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            className = classTypeElement.getQualifiedName().toString();
        }
        return ClassName.bestGuess(className);
    }

    public static ClassName bestGuessGeneric(RpcReturnType rpcReturnType) {
        if (rpcReturnType == null) {
            return ClassName.OBJECT;
        }
        String className;
        try {
            Class<?> type = rpcReturnType.generic();
            className = type.getCanonicalName();
        } catch (MirroredTypeException mte) {
            DeclaredType classTypeMirror = (DeclaredType) mte.getTypeMirror();
            TypeElement classTypeElement = (TypeElement) classTypeMirror.asElement();
            className = classTypeElement.getQualifiedName().toString();
        }
        return ClassName.bestGuess(className);
    }
}
