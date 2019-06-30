package com.afirez.irouter;

import java.io.IOException;
import java.lang.reflect.Array;

import static com.afirez.irouter.Utils.checkNotNull;

abstract class ParameterHandler<T> {
    abstract void apply(Request.Builder builder, T value) throws IOException;

    final ParameterHandler<Iterable<T>> iterable() {
        return new ParameterHandler<Iterable<T>>() {
            @Override void apply(Request.Builder builder, Iterable<T> values)
                    throws IOException {
                if (values == null) return; // Skip null values.

                for (T value : values) {
                    ParameterHandler.this.apply(builder, value);
                }
            }
        };
    }

    final ParameterHandler<Object> array() {
        return new ParameterHandler<Object>() {
            @Override void apply(Request.Builder builder, Object values) throws IOException {
                if (values == null) return; // Skip null values.

                for (int i = 0, size = Array.getLength(values); i < size; i++) {
                    ParameterHandler.this.apply(builder, (T) Array.get(values, i));
                }
            }
        };
    }

//    static final class Path<T> extends ParameterHandler<T> {
//        private final String name;
//        private final Converter<T, String> valueConverter;
//
//        Path(String name, Converter<T, String> valueConverter) {
//            this.name = checkNotNull(name, "name == null");
//            this.valueConverter = valueConverter;
//        }
//
//        @Override void apply(Request.Builder builder, T value) {
//            builder.path(value);
//        }
//    }

    static final class PathParam<T> extends ParameterHandler<T> {
        private final String name;
        private final Converter<T, String> valueConverter;

        PathParam(String name, Converter<T, String> valueConverter) {
            this.name = checkNotNull(name, "name == null");
            this.valueConverter = valueConverter;
        }

        @Override void apply(Request.Builder builder, T value) throws IOException {
            if (value == null) {
                throw new IllegalArgumentException(
                        "Path parameter \"" + name + "\" value must not be null.");
            }

//            String pathParam = valueConverter.convert(value);

            String pathParam = String.valueOf(value);

            builder.addPathParam(name, pathParam);
        }
    }

    static final class Query<T> extends ParameterHandler<T> {
        private final String name;
        private final Converter<T, String> valueConverter;
        public Query(String name, Converter<T, String> valueConverter) {
            this.name = checkNotNull(name, "name == null");
            this.valueConverter = valueConverter;
        }

        @Override
        void apply(Request.Builder builder, T value) throws IOException {
            if (value == null) return; // Skip null values.

//            String queryValue = valueConverter.convert(value);
//            if (queryValue == null) return; // Skip converted but null values

            builder.addQueryParam(name, value);
        }
    }


}
