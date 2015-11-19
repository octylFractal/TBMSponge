package me.kenzierocks.plugins.tbm;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Game;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.ImmutableDataManipulator;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.service.persistence.InvalidDataException;

import me.kenzierocks.plugins.tbm.spongeabc.DataUtil;
import me.kenzierocks.plugins.tbm.spongeabc.ImmutableDataCachingUtil;
import me.kenzierocks.plugins.tbm.spongeabc.SpongeValue;

public final class TBMKeys {

    private static final class TBMEntityData
            implements DataManipulator<TBMEntityData, ImmutableTBMEntityData> {

        private Boolean tbmEntity = Boolean.FALSE;

        private TBMEntityData() {
            this(Boolean.FALSE);
        }

        private TBMEntityData(Boolean value) {
            this.tbmEntity = value;
        }

        @Override
        public int compareTo(TBMEntityData o) {
            return this.tbmEntity.compareTo(o.tbmEntity);
        }

        @Override
        public DataContainer toContainer() {
            return new MemoryDataContainer().set(IS_TBM_ENTITY, this.tbmEntity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
            if (key.equals(IS_TBM_ENTITY)) {
                return (Optional<E>) Optional.of(this.tbmEntity);
            }
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
            if (key.equals(IS_TBM_ENTITY)) {
                return (Optional<V>) Optional.of(new SpongeValue<Boolean>(
                        IS_TBM_ENTITY, false, this.tbmEntity));
            }
            return Optional.empty();
        }

        @Override
        public boolean supports(Key<?> key) {
            return key.equals(IS_TBM_ENTITY);
        }

        @Override
        public Set<Key<?>> getKeys() {
            return Collections.singleton(IS_TBM_ENTITY);
        }

        @Override
        public Set<ImmutableValue<?>> getValues() {
            return Collections.singleton(
                    ImmutableDataCachingUtil.getValue(ImmutableValue.class,
                            IS_TBM_ENTITY, Boolean.FALSE, this.tbmEntity));
        }

        @Override
        public Optional<TBMEntityData> fill(DataHolder dataHolder,
                MergeFunction overlap) {
            if (!dataHolder.supports(IS_TBM_ENTITY)) {
                return Optional.empty();
            }
            TBMEntityData m = checkNotNull(overlap).merge(copy(),
                    from(dataHolder.toContainer()).orElse(null));
            return Optional.of(set(IS_TBM_ENTITY, m.get(IS_TBM_ENTITY).get()));
        }

        @Override
        public Optional<TBMEntityData> from(DataContainer container) {
            set(IS_TBM_ENTITY, DataUtil.getData(container, IS_TBM_ENTITY));
            return Optional.of(this);
        }

        @Override
        public <E> TBMEntityData set(Key<? extends BaseValue<E>> key, E value) {
            checkArgument(supports(key),
                    "This data manipulator doesn't support the following key: "
                            + key.toString());
            this.tbmEntity = (Boolean) value;
            return this;
        }

        @Override
        public TBMEntityData copy() {
            return new TBMEntityData(this.tbmEntity);
        }

        @Override
        public ImmutableTBMEntityData asImmutable() {
            return ImmutableDataCachingUtil.getManipulator(
                    ImmutableTBMEntityData.class, this.tbmEntity);
        }

    }

    private static final class ImmutableTBMEntityData implements
            ImmutableDataManipulator<ImmutableTBMEntityData, TBMEntityData> {

        private final Boolean tbmEntity;
        private final ImmutableValue<Boolean> value;

        private ImmutableTBMEntityData() {
            this(Boolean.FALSE);
        }

        private ImmutableTBMEntityData(Boolean value) {
            this.tbmEntity = value;
            this.value = ImmutableDataCachingUtil.getValue(ImmutableValue.class,
                    IS_TBM_ENTITY, Boolean.FALSE, value);
        }

        @Override
        public int compareTo(ImmutableTBMEntityData o) {
            return this.tbmEntity.compareTo(o.tbmEntity);
        }

        @Override
        public DataContainer toContainer() {
            return new MemoryDataContainer().set(IS_TBM_ENTITY, this.tbmEntity);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
            if (key.equals(IS_TBM_ENTITY)) {
                return (Optional<E>) Optional.of(this.tbmEntity);
            }
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
            if (key.equals(IS_TBM_ENTITY)) {
                return (Optional<V>) Optional.of(this.value);
            }
            return Optional.empty();
        }

        @Override
        public boolean supports(Key<?> key) {
            return key.equals(IS_TBM_ENTITY);
        }

        @Override
        public Set<Key<?>> getKeys() {
            return Collections.singleton(IS_TBM_ENTITY);
        }

        @Override
        public Set<ImmutableValue<?>> getValues() {
            return Collections.singleton(this.value);
        }

        @Override
        public <E> Optional<ImmutableTBMEntityData>
                with(Key<? extends BaseValue<E>> key, E value) {
            if (supports(key)) {
                return Optional.of(asMutable().set(key, value).asImmutable());
            }
            return Optional.empty();
        }

        @Override
        public TBMEntityData asMutable() {
            return new TBMEntityData(this.tbmEntity);
        }

    }

    @SuppressWarnings("unchecked")
    public static final Key<Value<Boolean>> IS_TBM_ENTITY =
            KeyFactory.makeSingleKey(Boolean.class, Value.class,
                    DataQuery.of("tbmEntity"));

    public static void registerKeyStuff(Game game) {
        game.getRegistry().getManipulatorRegistry().register(
                TBMEntityData.class, ImmutableTBMEntityData.class,
                new DataManipulatorBuilder<TBMEntityData, ImmutableTBMEntityData>() {

                    @Override
                    public Optional<TBMEntityData> build(DataView container)
                            throws InvalidDataException {
                        return container.get(IS_TBM_ENTITY.getQuery())
                                .map(Boolean.class::cast)
                                .map(TBMEntityData::new);
                    }

                    @Override
                    public TBMEntityData create() {
                        return new TBMEntityData();
                    }

                    @Override
                    public Optional<TBMEntityData>
                            createFrom(DataHolder dataHolder) {
                        return dataHolder.get(IS_TBM_ENTITY)
                                .map(Boolean.class::cast)
                                .map(TBMEntityData::new);
                    }

                });
    }

    private TBMKeys() {
    }

}
