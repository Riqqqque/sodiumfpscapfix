package com.rique.sodiumfpscapfix;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

public final class UnlockedFpsCapValueSet {
    private static final String VALUE_SET_CLASS = "net.minecraft.client.OptionInstance$ValueSet";

    private UnlockedFpsCapValueSet() {
    }

    public static Object wrap(Object delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("Missing FPS value set");
        }

        Class<?> valueSetType = valueSetType(delegate);
        InvocationHandler handler = new Handler(delegate);

        return Proxy.newProxyInstance(valueSetType.getClassLoader(), new Class<?>[]{valueSetType}, handler);
    }

    private static Class<?> valueSetType(Object delegate) {
        try {
            Class<?> valueSetType = Class.forName(VALUE_SET_CLASS, false, delegate.getClass().getClassLoader());

            if (!valueSetType.isInstance(delegate)) {
                throw new IllegalArgumentException("Unexpected FPS value set: " + delegate.getClass().getName());
            }

            return valueSetType;
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to find Minecraft option value set", exception);
        }
    }

    private static final class Handler implements InvocationHandler {
        private final Object delegate;

        private Handler(Object delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.getDeclaringClass() == Object.class) {
                return switch (method.getName()) {
                    case "equals" -> proxy == args[0];
                    case "hashCode" -> System.identityHashCode(proxy);
                    case "toString" -> "UnlockedFpsCapValueSet[" + this.delegate + "]";
                    default -> invokeDelegate(method, args);
                };
            }

            return switch (method.getName()) {
                case "validateValue" -> Optional.of(FpsCapSupport.clamp(args == null || args[0] == null ? FpsCapConstants.MIN_FPS_CAP : (Integer) args[0]));
                case "codec" -> FpsCapSupport.codec();
                default -> invokeDelegate(method, args);
            };
        }

        private Object invokeDelegate(Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(this.delegate, args);
            } catch (IllegalAccessException exception) {
                method.setAccessible(true);
                return method.invoke(this.delegate, args);
            } catch (InvocationTargetException exception) {
                throw exception.getCause();
            }
        }
    }
}
