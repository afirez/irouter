package com.afirez.irouter;

import android.util.Log;
import com.afirez.spi.ExtensionLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.afirez.irouter.Utils.checkNotNull;

public class IRouter implements Call.Factory {

    public static IRouter getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        static IRouter INSTANCE = new IRouter();
    }

    public static <T> T with(Class<T> service) {
        return IRouter.getInstance().create(service);
    }

    private IRouter() {
        callAdapterFactories = new ArrayList<>();
        callAdapterFactories.add(new DefaultCallAdapterFactory());
    }

    private static boolean debug = false;

    public static void setDebug(boolean debug) {
        IRouter.debug = debug;
    }

    private List<CallAdapter.Factory> callAdapterFactories;


    private List<Interceptor> interceptors;

    private Interceptor iRouterInterceptor;


    private final Map<Method, ServiceMethod<?, ?>> serviceMethodCache = new ConcurrentHashMap<>();

    private Call.Factory callFactory = this;

//    private String host = "app://IRouter";
//
//    public String host() {
//        return host;
//    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> service) {
        Utils.validateServiceInterface(service);

        return (T) Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args)
                            throws Throwable {
                        if (method.getDeclaringClass() == Object.class) {
                            return method.invoke(this, args);
                        }


                        ServiceMethod<Object, Object> serviceMethod =
                                (ServiceMethod<Object, Object>) loadServiceMethod(method);
//                        Request request = serviceMethod.toRequest(args);
//                        Call<Object> call = callFactory.newCall(request);
                        Call<Object> call = callFactory.newCall(serviceMethod, args);
                        return serviceMethod.adapt(call);
                    }
                });
    }

    ServiceMethod<?, ?> loadServiceMethod(Method method) {
        ServiceMethod<?, ?> result = serviceMethodCache.get(method);
        if (result != null) return result;

        synchronized (serviceMethodCache) {
            result = serviceMethodCache.get(method);
            if (result == null) {
                result = new ServiceMethod.Builder<>(this, method).build();
                serviceMethodCache.put(method, result);
            }
        }
        return result;
    }


    List<Interceptor> interceptors() {
        if (interceptors != null && !interceptors.isEmpty()) {
            return interceptors;
        }

        synchronized (this) {
            if (interceptors != null && !interceptors.isEmpty()) {
                return interceptors;
            }

            interceptors = new ArrayList<>();
            Map<String, Interceptor> interceptorMap = ExtensionLoader.getInstance().loadExtensions(Interceptor.class);
            if (interceptorMap != null) {
                Set<Map.Entry<String, Interceptor>> entries = interceptorMap.entrySet();
                for (Map.Entry<String, Interceptor> entry : entries) {
                    interceptors.add(entry.getValue());
                }
            }

            iRouterInterceptor = new IRouterInterceptor();

            interceptors.add(iRouterInterceptor);
        }

        return interceptors;
    }

    /**
     * Returns a list of the factories tried when creating a
     * {@linkplain #callAdapter(Type, Annotation[])} call adapter}.
     */
    public List<CallAdapter.Factory> callAdapterFactories() {
        return callAdapterFactories;
    }

    public CallAdapter<?, ?> callAdapter(Type returnType, Annotation[] annotations) {
        return nextCallAdapter(null, returnType, annotations);
    }

    /**
     * Returns the {@link CallAdapter} for {@code returnType} from the available {@linkplain
     * #callAdapterFactories() factories} except {@code skipPast}.
     *
     * @throws IllegalArgumentException if no call adapter available for {@code type}.
     */
    public CallAdapter<?, ?> nextCallAdapter(CallAdapter.Factory skipPast, Type returnType,
                                             Annotation[] annotations) {
        checkNotNull(returnType, "returnType == null");
        checkNotNull(annotations, "annotations == null");

        int start = callAdapterFactories.indexOf(skipPast) + 1;
        for (int i = start, count = callAdapterFactories.size(); i < count; i++) {
            CallAdapter<?, ?> adapter = callAdapterFactories.get(i).get(returnType, annotations, this);
            if (adapter != null) {
                return adapter;
            }
        }

        StringBuilder builder = new StringBuilder("Could not locate call adapter for ")
                .append(returnType)
                .append(".\n");
        if (skipPast != null) {
            builder.append("  Skipped:");
            for (int i = 0; i < start; i++) {
                builder.append("\n   * ").append(callAdapterFactories.get(i).getClass().getName());
            }
            builder.append('\n');
        }
        builder.append("  Tried:");
        for (int i = start, count = callAdapterFactories.size(); i < count; i++) {
            builder.append("\n   * ").append(callAdapterFactories.get(i).getClass().getName());
        }
        throw new IllegalArgumentException(builder.toString());
    }


    public Converter<?, String> stringConverter(Type iterableType, Annotation[] annotations) {
        return null; // no need now
    }

    @Override
    public <T> Call<T> newCall(ServiceMethod method, Object[] args) {
        return new RealCall<>(this, method, args);
    }

    @Override
    public <T> Call<T> newCall(Request request) {
        return new RealCall<>(this, request);
    }

    public static void log(String msg) {
        if (debug) {
            Log.w("IRouter", msg);
        }
    }

}
