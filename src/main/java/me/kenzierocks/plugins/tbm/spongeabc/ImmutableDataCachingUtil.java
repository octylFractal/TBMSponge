package me.kenzierocks.plugins.tbm.spongeabc;

import static org.apache.commons.lang3.ClassUtils.isAssignable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

public final class ImmutableDataCachingUtil {

    private ImmutableDataCachingUtil() {
    }

    public static final int CACHE_LIMIT_FOR_INDIVIDUAL_TYPE = 100;
    public static final int MANIPULATOR_CACHE_LIMIT = 100000;
    public static final int VALUE_CACHE_LIMIT = 100000;

    private static final Cache<String, ImmutableDataManipulator<?, ?>> manipulatorCache =
            CacheBuilder.newBuilder().maximumSize(MANIPULATOR_CACHE_LIMIT)
                    .concurrencyLevel(4).build();

    private static final Cache<String, ImmutableValue<?>> valueCache =
            CacheBuilder.newBuilder().concurrencyLevel(4)
                    .maximumSize(VALUE_CACHE_LIMIT).build();

    /**
     * Retrieves a basic manipulator from {@link Cache}. If the {@link Cache}
     * does not have the desired {@link ImmutableDataManipulator} with relative
     * values, a new one is created and submitted to the cache for future
     * retrieval.
     *
     * <p>
     * Note that two instances of an {@link ImmutableDataManipulator} may be
     * equal to each other, but they may not be the same instance, this is due
     * to caching and outside instantiation.
     * </p>
     *
     * @param immutableClass
     *            The immutable manipulator class to get an instance of
     * @param args
     *            The arguments to pass to the constructor
     * @param <T>
     *            The type of immutable data manipulator
     * @return The newly created immutable data manipulators
     */
    @SuppressWarnings("unchecked")
    public static <T extends ImmutableDataManipulator<?, ?>> T getManipulator(
            final Class<T> immutableClass, final Object... args) {
        final String key = getKey(immutableClass, args);
        // We can't really use the generic typing here because it's
        // complicated...
        try {
            // Let's get the key
            return (T) ImmutableDataCachingUtil.manipulatorCache.get(key,
                    (Callable<ImmutableDataManipulator<?, ?>>) () -> {
                        try {
                            return createUnsafeInstance(immutableClass, args);
                        } catch (InstantiationException | IllegalAccessException
                                | InvocationTargetException e) {
                            // TODO log?
                        }
                        throw new UnsupportedOperationException(
                                "Could not construct the ImmutableDataManipulator: "
                                        + immutableClass.getName()
                                        + " with the args: "
                                        + Arrays.toString(args));
                    });
        } catch (ExecutionException e) {
            throw new UnsupportedOperationException(
                    "Could not construct the ImmutableDataManipulator: "
                            + immutableClass.getName(),
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    public static <E, V extends ImmutableValue<?>, T extends ImmutableValue<E>>
            T getValue(final Class<V> valueClass,
                    final Key<? extends BaseValue<E>> usedKey,
                    final E defaultArg, final E arg,
                    final Object... extraArgs) {
        final String key = getKey(valueClass, usedKey.getQuery().asString('.'),
                arg.getClass(), arg);
        try {
            return (T) ImmutableDataCachingUtil.valueCache.get(key,
                    (Callable<ImmutableValue<?>>) () -> {
                        try {
                            if (extraArgs == null || extraArgs.length == 0) {
                                return createUnsafeInstance(valueClass, usedKey,
                                        defaultArg, arg);
                            } else {
                                return createUnsafeInstance(valueClass, usedKey,
                                        defaultArg, arg, extraArgs);
                            }
                        } catch (InstantiationException | IllegalAccessException
                                | InvocationTargetException e) {
                            // TODO log?
                        }
                        throw new UnsupportedOperationException(
                                "Could not construct the ImmutableValue: "
                                        + valueClass.getName());
                    });
        } catch (ExecutionException e) {
            throw new UnsupportedOperationException(
                    "Could not construct the ImmutableValue: "
                            + valueClass.getName(),
                    e);
        }
    }

    public static <T> T createUnsafeInstance(final Class<T> objectClass,
            Object... args) throws IllegalAccessException,
                    InvocationTargetException, InstantiationException {
        if (args == null) {
            args = new Object[] { null };
        }
        final Constructor<T> tConstructor = findConstructor(objectClass, args);
        try {
            return tConstructor.newInstance(args);
        } catch (Exception e) {
            final Object[] deconstructedArgs = deconstructArray(args).toArray();
            return tConstructor.newInstance(deconstructedArgs);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> Constructor<T> findConstructor(final Class<T> objectClass,
            Object... args) {
        final Constructor<?>[] ctors = objectClass.getConstructors();
        if (args == null) {
            args = new Object[] { null };
        }
        // labeled loops
        dance: for (final Constructor<?> ctor : ctors) {
            final Class<?>[] paramTypes = ctor.getParameterTypes();
            if (paramTypes.length != args.length) {
                for (Object object : args) {
                    if (object != null) { // hahahah
                        if (object.getClass().isArray()) {
                            final Object[] objects =
                                    deconstructArray(args).toArray();
                            return findConstructor(objectClass, objects);
                        }
                    }
                }
                continue; // we haven't found the right constructor
            }
            for (int i = 0; i < paramTypes.length; i++) {
                final Class<?> parameter = paramTypes[i];
                if (!isAssignable(args[i] == null ? null : args[i].getClass(),
                        parameter, true)) {
                    continue dance; // continue the outer loop since we didn't
                                    // find the right one
                }
            }
            // We've found the right constructor, now to actually construct it!
            return (Constructor<T>) ctor;
        }
        throw new IllegalArgumentException(
                "Applicable constructor not found for class: "
                        + objectClass.getCanonicalName() + " with args: "
                        + Arrays.toString(args));
    }

    private static List<Object> deconstructArray(Object[] objects) {
        final List<Object> list = new ArrayList<>();
        for (Object object : objects) {
            if (object == null) {
                list.add(null);
                continue;
            }
            if (object.getClass().isArray()) {
                list.addAll(deconstructArray((Object[]) object));
            } else {
                list.add(object);
            }
        }
        return list;
    }

    private static String getKey(final Class<?> immutableClass,
            final Object... args) {
        final StringBuilder builder =
                new StringBuilder(immutableClass.getCanonicalName() + ":");
        for (Object object : args) {
            if (object instanceof CatalogType) {
                builder.append("{").append(((CatalogType) object).getId())
                        .append("}");
            } else {
                builder.append("{").append(object.toString()).append("}");
            }
        }
        return builder.toString();
    }
}