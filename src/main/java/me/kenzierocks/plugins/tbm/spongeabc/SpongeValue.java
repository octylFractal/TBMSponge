package me.kenzierocks.plugins.tbm.spongeabc;

import java.util.function.Function;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

public class SpongeValue<E> extends AbstractBaseValue<E> implements Value<E> {

    public SpongeValue(Key<? extends BaseValue<E>> key, E defaultValue) {
        this(key, defaultValue, defaultValue);
    }

    public SpongeValue(Key<? extends BaseValue<E>> key, E defaultValue,
            E actualValue) {
        super(key, defaultValue, actualValue);
    }

    @Override
    public Value<E> set(E value) {
        this.actualValue = value;
        return this;
    }

    @Override
    public Value<E> transform(Function<E, E> function) {
        this.actualValue = function.apply(this.actualValue);
        return this;
    }

    @Override
    public ImmutableValue<E> asImmutable() {
        return ImmutableSpongeValue.cachedOf(this.getKey(), this.getDefault(),
                this.actualValue);
    }
}