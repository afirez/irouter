package com.afirez.irouter;

import com.afirez.irouter.router.Action;
import com.afirez.irouter.router.Path;
import com.afirez.irouter.router.Query;
import com.afirez.irouter.router.Uri;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServiceMethod<R, T> {

    static final String PARAM = "[a-zA-Z][a-zA-Z0-9_-]*";
    static final Pattern PARAM_URL_REGEX = Pattern.compile("\\{(" + PARAM + ")\\}");
    static final Pattern PARAM_NAME_REGEX = Pattern.compile(PARAM);

    private final CallAdapter<R, T> callAdapter;

    private final Type responseType;

    private final boolean hasPath;
    private final boolean hasAction;
    private final boolean hasUri;
    private final boolean hasQuery;
    private final boolean hasPathParam;

    private final String path;
    private final Set<String> pathParamNames;
    private final String action;
    private final String uri;

    private final ParameterHandler<?>[] parameterHandlers;

    public ServiceMethod(Builder<R, T> builder) {
        this.callAdapter = builder.callAdapter;
        this.responseType = builder.responseType;
        this.hasPath = builder.hasPath;
        this.hasUri = builder.hasUri;
        this.hasAction = builder.hasAction;
        this.hasQuery = builder.hasQuery;
        this.hasPathParam = builder.hasPathParam;
        this.path = builder.path;
        this.uri = builder.uri;
        this.action = builder.action;
        this.pathParamNames = builder.pathParamNames;
        this.parameterHandlers = builder.parameterHandlers;
    }


    public T adapt(Call<R> call) {
        return callAdapter.adapt(call);
    }

    public Request toRequest(Object[] args) throws IOException {
        Request.Builder requestBuilder = new Request.Builder();

        requestBuilder.path(path);
        requestBuilder.uri(uri);
        requestBuilder.action(action);

        ParameterHandler<Object>[] handlers = (ParameterHandler<Object>[]) this.parameterHandlers;
        int argumentCount = args != null ? args.length : 0;
        if (argumentCount != handlers.length) {
            throw new IllegalArgumentException("Argument count (" + argumentCount
                    + ") doesn't match expected count (" + handlers.length + ")");
        }

        for (int p = 0; p < argumentCount; p++) {
            handlers[p].apply(requestBuilder, args[p]);
        }


        return requestBuilder.build();
    }

//    /** Builds a method return value from an HTTP onResult body. */
//    R toResponse(ResponseBody body) throws IOException {
//        return responseConverter.convert(body);
//    }


    static final class Builder<T, R> {
        final IRouter IRouter;
        final Method method;
        final Annotation[] methodAnnotations;
        final Annotation[][] parameterAnnotations;
        final Type[] parameterTypes;

        Type responseType;

        boolean hasPath;
        boolean hasAction;
        boolean hasUri;
        boolean hasQuery;
        boolean hasPathParam;

        String path;
        String uri;
        String action;
        Set<String> pathParamNames;
        ParameterHandler<?>[] parameterHandlers;

        CallAdapter<T, R> callAdapter;


        Builder(IRouter IRouter, Method method) {
            this.IRouter = IRouter;
            this.method = method;
            this.methodAnnotations = method.getAnnotations();
            this.parameterTypes = method.getGenericParameterTypes();
            this.parameterAnnotations = method.getParameterAnnotations();
        }

        public ServiceMethod build() {
            callAdapter = createCallAdapter();
            responseType = callAdapter.responseType();

//            responseConverter = createResponseConverter();

            for (Annotation annotation : methodAnnotations) {
                parseMethodAnnotation(annotation);
            }

            int paramLength = parameterAnnotations.length;

            parameterHandlers = new ParameterHandler<?>[paramLength];

            for (int i = 0; i < paramLength; i++) {
                Type parameterType = parameterTypes[i];
                if (Utils.hasUnresolvableType(parameterType)) {
                    throw parameterError(i, "Parameter type must not include a type variable or wildcard: %s",
                            parameterType);
                }

                Annotation[] parameterAnnotationList = parameterAnnotations[i];
                if (parameterAnnotationList == null) {
                    throw parameterError(i, "No IRouter annotation found.");
                }

                parameterHandlers[i] = parseParameter(i, parameterType, parameterAnnotationList);
            }

            if (hasPath && hasUri) {
                throw methodError("invalidate method annotation @Path and @Uri.");
            }

            if (hasPath && (path == null || path.isEmpty())) {
                throw methodError("invalidate method annotation @Path.");
            }

            if (hasUri && (uri == null || uri.isEmpty())) {
                throw methodError("invalidate method annotation @Uri.");
            }

            return new ServiceMethod<>(this);
        }

        private CallAdapter<T, R> createCallAdapter() {
            Type returnType = method.getGenericReturnType();
            if (Utils.hasUnresolvableType(returnType)) {
                throw methodError(
                        "Method return type must not include a type variable or wildcard: %s", returnType);
            }
//            if (returnType == void.class) {
//                throw methodError("Service methods cannot return void.");
//            }
            Annotation[] annotations = method.getAnnotations();
            try {
                //noinspection unchecked
                return (CallAdapter<T, R>) IRouter.callAdapter(returnType, annotations);
            } catch (RuntimeException e) { // Wide exception range because factories are user code.
                throw methodError(e, "Unable to create call adapter for %s", returnType);
            }
        }

        private RuntimeException methodError(String message, Object... args) {
            return methodError(null, message, args);
        }

        private RuntimeException methodError(Throwable cause, String message, Object... args) {
            message = String.format(message, args);
            return new IllegalArgumentException(message
                    + "\n    for method "
                    + method.getDeclaringClass().getSimpleName()
                    + "."
                    + method.getName(), cause);
        }

        private RuntimeException parameterError(int i, String message, Object... args) {
            return methodError(message + " (parameter #" + (i + 1) + ")", args);
        }

        private void parseMethodAnnotation(Annotation annotation) {
            if (annotation instanceof Path) {
                hasPath = true;
                parsePath(((Path) annotation).value());
            } else if (annotation instanceof Action) {
                hasAction = true;
                parseAction(((Action) annotation).value());
            } else if (annotation instanceof Uri) {
                hasUri = true;
                parseUri(((Uri) annotation).value());
            }
        }

        private void parsePath(String path) {
            if (path.isEmpty()) {
                return;
            }

            this.path = path;
            this.pathParamNames = parsePathParameters(path);
        }

        private void parseAction(String action) {
            if (action.isEmpty()) {
                return;
            }
            this.action = action;
        }

        private void parseUri(String uri) {
            if (uri.isEmpty()) {
                return;
            }
            this.uri = uri;
            this.pathParamNames = parsePathParameters(uri);
        }

        private ParameterHandler<?> parseParameter(
                int i, Type parameterType, Annotation[] annotations) {
            ParameterHandler<?> result = null;
            for (Annotation annotation : annotations) {
                ParameterHandler<?> annotationAction = parseParameterAnnotation(
                        i, parameterType, annotations, annotation);

                if (annotationAction == null) {
                    continue;
                }

                if (result != null) {
                    throw parameterError(i, "Multiple IRouter annotations found, only one allowed.");
                }

                result = annotationAction;
            }

            if (result == null) {
                throw parameterError(i, "No IRouter annotation found.");
            }

            return result;
        }

        private ParameterHandler<?> parseParameterAnnotation(
                int i, Type type, Annotation[] annotations, Annotation annotation) {
            if (annotation instanceof Path) {
                if (!hasPath && !hasUri) {
                    throw parameterError(i, "@Path on param can only be used with path on @Path on method or uri on @Uri");
                }

                if (hasPath && (path == null || path.isEmpty())) {
                    throw parameterError(i, "@Path can only be used with path on @Path");
                }

                if (hasUri && (uri == null || uri.isEmpty())) {
                    throw parameterError(i, "@Path can only be used with uri on @Uri");
                }

                hasPathParam = true;
                Path path = (Path) annotation;
                String name = path.value();
                validatePathName(i, name);
                Converter<?, String> converter = IRouter.stringConverter(type, annotations);
                return new ParameterHandler.PathParam<>(name, converter);
            } else if (annotation instanceof Query) {
                Query query = (Query) annotation;
                String name = query.value();
                hasQuery = true;

                Converter<?, String> converter = IRouter.stringConverter(type, annotations);
                return new ParameterHandler.Query<>(name, converter);

//                Class<?> rawParamType = Utils.getRawType(type);
//
//                if (Iterable.class.isAssignableFrom(rawParamType)) {
//                    if (!(type instanceof ParameterizedType)) {
//                        throw parameterError(i, rawParamType.getSimpleName()
//                                + " must include generic type (e.g., "
//                                + rawParamType.getSimpleName()
//                                + "<String>)");
//                    }
//
//                    ParameterizedType parameterizedType = (ParameterizedType) type;
//                    Type iterableType = Utils.getParameterUpperBound(0, parameterizedType);
//                    Converter<?, String> converter = IRouter.stringConverter(iterableType, annotations);
//                    return new ParameterHandler.Query<>(name, converter).iterable();
//                } else if (rawParamType.isArray()) {
//                    Class<?> arrayComponentType = boxIfPrimitive(rawParamType.getComponentType());
//                    Converter<?, String> converter = IRouter.stringConverter(arrayComponentType, annotations);
//                    return new ParameterHandler.Query<>(name, converter).array();
//                } else {
//                    Converter<?, String> converter = IRouter.stringConverter(type, annotations);
//                    return new ParameterHandler.Query<>(name, converter);
//                }

            }

            return null; // Not a IRouter annotation.
        }

        private void validatePathName(int p, String name) {
            if (!PARAM_NAME_REGEX.matcher(name).matches()) {
                throw parameterError(p, "@Path parameter name must match %s. Found: %s",
                        PARAM_URL_REGEX.pattern(), name);
            }
            // Verify URL replacement name is actually present in the URL path.
            if (hasPath && !pathParamNames.contains(name)) {
                throw parameterError(p, "Path \"%s\" does not contain \"{%s}\".", path, name);
            }

            if (hasUri && !pathParamNames.contains(name)) {
                throw parameterError(p, "Uri \"%s\" does not contain \"{%s}\".", path, name);
            }
        }
    }


    static Set<String> parsePathParameters(String path) {
        Matcher m = PARAM_URL_REGEX.matcher(path);
        Set<String> patterns = new LinkedHashSet<>();
        while (m.find()) {
            patterns.add(m.group(1));
        }
        return patterns;
    }

    static Class<?> boxIfPrimitive(Class<?> type) {
        if (boolean.class == type) return Boolean.class;
        if (byte.class == type) return Byte.class;
        if (char.class == type) return Character.class;
        if (double.class == type) return Double.class;
        if (float.class == type) return Float.class;
        if (int.class == type) return Integer.class;
        if (long.class == type) return Long.class;
        if (short.class == type) return Short.class;
        return type;
    }
}
