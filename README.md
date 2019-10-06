# NettyRpc
基于Netty HTTP协议实现的轻量级RPC框架

## 1 集成说明
## 1.1 在builde.gradle(Project)下添加仓库地址：
```grovvy
allprojects {
    repositories {
        maven {
            url uri('https://raw.githubusercontent.com/kyle-android/netty-rpc/master/repo')
        }
    }
}
```
## 1.2 在builde.gradle(Module)下添加依赖：
```grovvy
implementation 'cn.kyle.support:rpc-core:1.0.0'
annotationProcessor 'cn.kyle.support:rpc-compiler:1.0.2'
```

## 2 使用说明
### 2.1 定义RPC接口文件

```java
@RpcService(ip = "127.0.0.1", port = 8094)
public interface UiAutomator {

    @RpcMethod
    boolean uiKeyClick(@RpcParam int x, @RpcParam int y) throws RemoteException;

    @RpcMethod
    @RpcReturnType(type = String.class)
    String getUserPhone(@RpcParam(type = String.class) String userId) throws RemoteException;

    @RpcMethod
    @RpcReturnType(type = List.class, generic = String.class)
    List<String> getUserPhoneList(@RpcParam(type = List.class, generic = String.class) List<String> userIds) throws RemoteException;
}
```

### 2.2 使用编译生成的RPC文件实现服务端

```java
public class UiAutomatorImpl extends UiAutomatorRpc.Stub {

    @Override
    public boolean uiKeyClick(int x, int y) throws RemoteException {
        System.out.println("UiAutomatorImpl execute uiKeyClick");
        return true;
    }

    @Override
    public String getUserPhone(String userId) throws RemoteException {
        System.out.println("UiAutomatorImpl execute getUserPhone");
        return "Kyle";
    }

    @Override
    public List<String> getUserPhoneList(List<String> userIds) throws RemoteException {
        System.out.println("UiAutomatorImpl execute getUserPhoneList");
        return Arrays.asList("Kyle");
    }
}
```

### 2.3 启动服务端

```java
 NettyService.getDefault().addService(new UiAutomatorImpl()).start(8094);
```

### 2.4 客户端发起RPC调用

```java
 UiAutomator uiAutomator = NettyRpc.create(UiAutomator.class);
 try {
    boolean result = uiAutomator.uiKeyClick(2, 3);
 } catch (RemoteException e) {
    e.printStackTrace();
 }
```
