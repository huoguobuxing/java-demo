package proxy;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;
import java.lang.reflect.Method;

// 动态代理，被代理的对象时不确定的，采用继承类，并增强public方法来实现的
public class CustomCglibProxy implements MethodInterceptor {
    public Object createProxyObject(Class<?> clazz){
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(this);
        return enhancer.create();
    }

    @Override
    public Object intercept(Object o, Method method, Object[] objects, MethodProxy methodProxy) throws Throwable {
        System.out.println("开始执行方法" + method.getName());
        Object o1 = methodProxy.invokeSuper(o, objects);
        System.out.println("结束执行方法"+method.getName());
        return o1;
    }
}
