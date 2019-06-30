package com.afirez.irouter;

import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Request {

    public static final int CALL_UNKNOWN = -1;
    public static final int CALL_ACTIVITY = 0;
    public static final int CALL_FRAGMENT = 1;
    public static final int CALL_SERVICE = 2;

    int call = CALL_UNKNOWN;
    Class<?> target;

    private String path;
    private String uri;
    private String action;

    private Intent intent;

    private Request(Builder builder) {
        path = builder.path;
        uri = builder.uri;
        action = builder.action;
        intent = builder.intent == null ? new Intent() : builder.intent;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public final static class Builder {

        private String path;
        private String uri;
        private String action;

        private Intent intent;

        public Builder() {
            intent = new Intent();
        }

        public Builder(Request request) {
            path = request.path;
            uri = request.uri;
            action = request.action;
            intent = request.intent == null ? new Intent() : request.intent;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

        public Builder uri(String uri) {
            this.uri = uri;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder addQueryParam(String name, Object queryValue) {
            if (queryValue == null) {
                return this;
            }

            if (queryValue instanceof Boolean) {
                intent.putExtra(name, (boolean) queryValue);
            } else if (queryValue instanceof Byte) {
                intent.putExtra(name, (byte) queryValue);
            } else if (queryValue instanceof Short) {
                intent.putExtra(name, (short) queryValue);
            } else if (queryValue instanceof Integer) {
                intent.putExtra(name, (int) queryValue);
            } else if (queryValue instanceof Float) {
                intent.putExtra(name, (float) queryValue);
            } else if (queryValue instanceof Double) {
                intent.putExtra(name, (double) queryValue);
            } else if (queryValue instanceof Parcelable) {
                intent.putExtra(name, (Parcelable) queryValue);
            } else if (queryValue instanceof Serializable) {
                intent.putExtra(name, (Serializable) queryValue);
            }else if (queryValue instanceof IBinder) {
                if (intent.getExtras() != null) {
                    intent.getExtras().putBinder(name, (IBinder) queryValue);
                }
            } else {
                Class<?> valueClass = queryValue.getClass();
                Class<?> rawType = Utils.getRawType(valueClass);
                if (ArrayList.class.isAssignableFrom(rawType)) {
                    try {
                        Type responseType = Utils.getCallResponseType(valueClass.getGenericSuperclass());
                        if (responseType == Integer.class) {
                            intent.putIntegerArrayListExtra(name, (ArrayList<Integer>) queryValue);
                        } else if (responseType == String.class) {
                            intent.putStringArrayListExtra(name, (ArrayList<String>) queryValue);
                        } else {
                            intent.putParcelableArrayListExtra(name, (ArrayList<? extends Parcelable>) queryValue);
                        }
                    } catch (Throwable e) {
                        IRouter.log("----> error: " + e);
                    }
                } else if (valueClass == boolean[].class) {
                    intent.putExtra(name, (boolean[]) queryValue);
                } else if (valueClass == byte[].class) {
                    intent.putExtra(name, (int[]) queryValue);
                } else if (valueClass == short[].class) {
                    intent.putExtra(name, (short[]) queryValue);
                } else if (valueClass == int[].class) {
                    intent.putExtra(name, (int[]) queryValue);
                } else if (valueClass == long[].class) {
                    intent.putExtra(name, (long[]) queryValue);
                } else if (valueClass == float[].class) {
                    intent.putExtra(name, (float[]) queryValue);
                } else if (valueClass == double[].class) {
                    intent.putExtra(name, (double[]) queryValue);
                } else if (valueClass == String[].class) {
                    intent.putExtra(name, (String[]) queryValue);
                } else if (valueClass == Parcelable[].class) {
                    intent.putExtra(name, (Parcelable[]) queryValue);
                }

            }

            return this;
        }

        Builder addPathParam(String name, String pathParam) {
            if (path != null) {
                path = path.replace("{" + name + "}", pathParam);
            }
            if (uri != null) {
                uri = uri.replace("{" + name + "}", pathParam);
            }
            return this;
        }

        public Request build() {
            intent = intent;

            return new Request(this);
        }
    }

    public String path() {
        return path;
    }

    public String uri() {
        return uri;
    }

    public String action() {
        return action;
    }


    public Intent intent() {
        return intent;
    }

    @Override
    public String toString() {
        return "Request{" +
                "call=" + call +
                ", path='" + path + '\'' +
                ", uri='" + uri + '\'' +
                ", action='" + action + '\'' +
                ", intent=" + intent +
                '}';
    }
}
