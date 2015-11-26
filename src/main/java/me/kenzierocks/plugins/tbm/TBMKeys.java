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
import org.spongepowered.api.data.value.ValueFactory;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.service.persistence.InvalidDataException;

import me.kenzierocks.plugins.tbm.spongeabc.DataUtil;

public final class TBMKeys {

    private static final ValueFactory VALUE_FACTORY =
            TBMPlugin.getInstance().getGame().getRegistry().getValueFactory();

    public static final class TBMTaggedData
            implements DataManipulator<TBMTaggedData, ImmutableTBMTaggedData> {

        private Boolean tbmTagged = Boolean.FALSE;

        private TBMTaggedData() {
            this(Boolean.FALSE);
        }

        private TBMTaggedData(Boolean value) {
            this.tbmTagged = value;
        }

        @Override
        public int compareTo(TBMTaggedData o) {
            return this.tbmTagged.compareTo(o.tbmTagged);
        }

        @Override
        public DataContainer toContainer() {
            return new MemoryDataContainer().set(TBM_TAGGED, this.tbmTagged);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
            if (key.equals(TBM_TAGGED)) {
                return (Optional<E>) Optional.of(this.tbmTagged);
            }
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
            if (key.equals(TBM_TAGGED)) {
                return (Optional<V>) Optional.of(VALUE_FACTORY.createValue(
                        TBM_TAGGED, this.tbmTagged, Boolean.FALSE));
            }
            return Optional.empty();
        }

        @Override
        public boolean supports(Key<?> key) {
            return key.equals(TBM_TAGGED);
        }

        @Override
        public Set<Key<?>> getKeys() {
            return Collections.singleton(TBM_TAGGED);
        }

        @Override
        public Set<ImmutableValue<?>> getValues() {
            return Collections
                    .singleton(getValue(TBM_TAGGED).get().asImmutable());
        }

        @Override
        public Optional<TBMTaggedData> fill(DataHolder dataHolder,
                MergeFunction overlap) {
            if (!dataHolder.supports(TBM_TAGGED)) {
                return Optional.empty();
            }
            TBMTaggedData m = checkNotNull(overlap).merge(copy(),
                    from(dataHolder.toContainer()).orElse(null));
            return Optional.of(set(TBM_TAGGED, m.get(TBM_TAGGED).get()));
        }

        @Override
        public Optional<TBMTaggedData> from(DataContainer container) {
            set(TBM_TAGGED, DataUtil.getData(container, TBM_TAGGED));
            return Optional.of(this);
        }

        @Override
        public <E> TBMTaggedData set(Key<? extends BaseValue<E>> key, E value) {
            checkArgument(supports(key),
                    "This data manipulator doesn't support the following key: "
                            + key.toString());
            this.tbmTagged = (Boolean) value;
            return this;
        }

        @Override
        public TBMTaggedData copy() {
            return new TBMTaggedData(this.tbmTagged);
        }

        @Override
        public ImmutableTBMTaggedData asImmutable() {
            return this.tbmTagged ? ImmutableTBMTaggedData.TRUE
                    : ImmutableTBMTaggedData.FALSE;
        }

    }

    public static final class ImmutableTBMTaggedData implements
            ImmutableDataManipulator<ImmutableTBMTaggedData, TBMTaggedData> {

        private static final ImmutableTBMTaggedData TRUE;
        private static final ImmutableTBMTaggedData FALSE;
        private static final ImmutableValue<Boolean> VALUE_TRUE;
        private static final ImmutableValue<Boolean> VALUE_FALSE;

        static {
            ValueFactory vF = TBMPlugin.getInstance().getGame().getRegistry()
                    .getValueFactory();
            VALUE_TRUE = vF.createValue(TBM_TAGGED, Boolean.TRUE, Boolean.FALSE)
                    .asImmutable();
            VALUE_FALSE =
                    vF.createValue(TBM_TAGGED, Boolean.FALSE, Boolean.FALSE)
                            .asImmutable();
            TRUE = new ImmutableTBMTaggedData(Boolean.TRUE);
            FALSE = new ImmutableTBMTaggedData(Boolean.FALSE);
        }

        private final Boolean tbmTagged;
        private final ImmutableValue<Boolean> value;

        private ImmutableTBMTaggedData() {
            this(Boolean.FALSE);
        }

        private ImmutableTBMTaggedData(Boolean value) {
            this.tbmTagged = value;
            this.value = value ? VALUE_TRUE : VALUE_FALSE;
        }

        @Override
        public int compareTo(ImmutableTBMTaggedData o) {
            return this.tbmTagged.compareTo(o.tbmTagged);
        }

        @Override
        public DataContainer toContainer() {
            return new MemoryDataContainer().set(TBM_TAGGED, this.tbmTagged);
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E> Optional<E> get(Key<? extends BaseValue<E>> key) {
            if (key.equals(TBM_TAGGED)) {
                return (Optional<E>) Optional.of(this.tbmTagged);
            }
            return Optional.empty();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <E, V extends BaseValue<E>> Optional<V> getValue(Key<V> key) {
            if (key.equals(TBM_TAGGED)) {
                return (Optional<V>) Optional.of(this.value);
            }
            return Optional.empty();
        }

        @Override
        public boolean supports(Key<?> key) {
            return key.equals(TBM_TAGGED);
        }

        @Override
        public Set<Key<?>> getKeys() {
            return Collections.singleton(TBM_TAGGED);
        }

        @Override
        public Set<ImmutableValue<?>> getValues() {
            return Collections.singleton(this.value);
        }

        @Override
        public <E> Optional<ImmutableTBMTaggedData>
                with(Key<? extends BaseValue<E>> key, E value) {
            if (supports(key)) {
                return Optional.of(asMutable().set(key, value).asImmutable());
            }
            return Optional.empty();
        }

        @Override
        public TBMTaggedData asMutable() {
            return new TBMTaggedData(this.tbmTagged);
        }

    }

    public static final Key<Value<Boolean>> TBM_TAGGED = KeyFactory
            .makeSingleKey(Boolean.class, Value.class, DataQuery.of("tbmTag"));

    public static void registerKeyStuff(Game game) {
        game.getManipulatorRegistry().register(TBMTaggedData.class,
                ImmutableTBMTaggedData.class,
                new DataManipulatorBuilder<TBMTaggedData, ImmutableTBMTaggedData>() {

                    @Override
                    public Optional<TBMTaggedData> build(DataView container)
                            throws InvalidDataException {
                        return container.get(TBM_TAGGED.getQuery())
                                .map(Boolean.class::cast)
                                .map(TBMTaggedData::new);
                    }

                    @Override
                    public TBMTaggedData create() {
                        return new TBMTaggedData();
                    }

                    @Override
                    public Optional<TBMTaggedData>
                            createFrom(DataHolder dataHolder) {
                        return dataHolder.get(TBM_TAGGED)
                                .map(Boolean.class::cast)
                                .map(TBMTaggedData::new);
                    }

                });
    }

    private TBMKeys() {
    }

}
