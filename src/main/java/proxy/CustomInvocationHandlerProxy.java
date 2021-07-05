package proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

// 动态代理，被代理的对象时不确定的，采用实现接口，并持有被代理对象，从而增强方法来实现
public class CustomInvocationHandlerProxy implements InvocationHandler {
    private Object delegatedObject;

    public CustomInvocationHandlerProxy(Object delegatedObject) {
        this.delegatedObject = delegatedObject;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("开始执行方法" + method.getName());
        Object result = method.invoke(delegatedObject, args);
        System.out.println("结束执行方法" + method.getName());
        return result;
    }
}
