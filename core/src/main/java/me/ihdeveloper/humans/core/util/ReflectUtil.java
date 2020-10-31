package me.ihdeveloper.humans.core.util;

import net.minecraft.server.v1_8_R3.DataWatcher;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public final class ReflectUtil {

    /**
     * A reflection helper for {@link DataWatcher}
     */
    public static final class NMSDataWatcher {

        public static boolean update(DataWatcher dataWatcher, int key, Object value) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
            Class<? extends DataWatcher> dataWatcherClass = dataWatcher.getClass();

            // Execute our custom update method
            Method dataWatcher$getWatchedObject = dataWatcherClass.getDeclaredMethod("j", int.class);
            dataWatcher$getWatchedObject.setAccessible(true);
            Object watchableObject = dataWatcher$getWatchedObject.invoke(dataWatcher, key);
            Class<?> watchableObjectClass = watchableObject.getClass();

            Method watchableObject$getObject = watchableObjectClass.getMethod("b");
            watchableObject$getObject.setAccessible(true);
            Object currentValue = watchableObject$getObject.invoke(watchableObject);
            if (value == currentValue)
                return false;

            Method watchableObject$setObject = watchableObjectClass.getMethod("a", Object.class);
            watchableObject$setObject.setAccessible(true);
            watchableObject$setObject.invoke(watchableObject, value);

            Method watchableObject$setWatched = watchableObjectClass.getMethod("a", boolean.class);
            watchableObject$setWatched.setAccessible(true);
            watchableObject$setWatched.invoke(watchableObject, true);

            // Invoke the object change event
            Field dataWatcher$objectChanged = dataWatcherClass.getDeclaredField("e");
            dataWatcher$objectChanged.setAccessible(true);
            dataWatcher$objectChanged.set(dataWatcher, true);

            return true;
        }
    }

    /**
     * Invokes private method in the object
     *
     * @param obj The object to invoke the method inside it
     * @param name The name of the private method
     * @param args The arguments of the method
     * @return The result of the invocation of the private method in the object
     *
     * @throws NoSuchMethodException When the method is not found
     * @throws InvocationTargetException If the private method invokes an exception
     * @throws IllegalAccessException If the access to the method is not allowed
     */
    public static Object invokePrivateMethod(Object obj, String name, Class<?>[] argsClass, Object... args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = obj.getClass().getDeclaredMethod(name, argsClass);
        method.setAccessible(true);
        return method.invoke(obj, args);
    }

}
