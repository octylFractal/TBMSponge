package me.kenzierocks.plugins.tbm.spongeabc;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

public class ImmutableSpongeValue<E> extends AbstractBaseValue<E>
        implements ImmutableValue<E> {

    /**
     * Gets a cached {@link ImmutableValue} of the default value and the actual
     * value.
     *
     * @param key
     *            The key for the value
     * @param defaultValue
     *            The default value
     * @param actualValue
     *            The actual value
     * @param <T>
     *            The type of value
     * @return The cached immutable value
     */
    public static <T> ImmutableValue<T> cachedOf(
            Key<? extends BaseValue<T>> key, T defaultValue, T actualValue) {
        return ImmutableDataCachingUtil.getValue(ImmutableSpongeValue.class,
                key, defaultValue, actualValue);
    }

    public ImmutableSpongeValue(Key<? extends BaseValue<E>> key,
            E defaultValue) {
        super(key, defaultValue, defaultValue);
    }

    public ImmutableSpongeValue(Key<? extends BaseValue<E>> key, E defaultValue,
            E actualValue) {
        super(key, defaultValue, actualValue);
    }

    @Override
    public ImmutableValue<E> with(E value) {
        return new ImmutableSpongeValue<>(this.getKey(), getDefault(), value);
    }

    @Override
    public ImmutableValue<E> transform(Function<E, E> function) {
        final E value = checkNotNull(function).apply(get());
        return new ImmutableSpongeValue<>(this.getKey(), getDefault(), value);
    }

    @Override
    public Value<E> asMutable() {
        return new SpongeValue<>(getKey(), getDefault(), get());
    }
}