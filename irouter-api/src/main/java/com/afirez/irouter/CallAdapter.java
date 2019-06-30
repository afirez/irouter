package com.afirez.irouter;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public interface CallAdapter<R, T> {

    Type responseType();


    T adapt(Call<R> call);

    abstract class Factory {
        /**
         * Returns a call adapter for interface methods that return {@code returnType}, or null if it
         * cannot be handled by this factory.
         */
        public abstract CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, IRouter IRouter);

        /**
         * Extract the upper bound of the generic parameter at {@code index} from {@code type}. For
         * example, index 1 of {@code Map<String, ? extends Runnable>} returns {@code Runnable}.
         */
        protected static Type getParameterUpperBound(int index, ParameterizedType type) {
            return Utils.getParameterUpperBound(index, type);
        }

        /**
         * Extract the raw class type from {@code type}. For example, the type representing
         * {@code List<? extends Runnable>} returns {@code List.class}.
         */
        protected static Class<?> getRawType(Type type) {
            return Utils.getRawType(type);
        }
    }
}

