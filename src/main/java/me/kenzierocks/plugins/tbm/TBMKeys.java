package me.kenzierocks.plugins.tbm;

import java.util.Optional;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.key.KeyFactory;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableBooleanData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractBooleanData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.persistence.InvalidDataException;

public final class TBMKeys {

    public static final class TBMTaggedData
            extends AbstractBooleanData<TBMTaggedData, ImmutableTBMTaggedData> {

        public TBMTaggedData(boolean value) {
            super(value, TBM_TAGGED, false);
        }

        @Override
        public Optional<TBMTaggedData> fill(DataHolder dataHolder,
                MergeFunction overlap) {
            if (!dataHolder.supports(TBM_TAGGED)) {
                return Optional.empty();
            }
            TBMTaggedData result = overlap.merge(this, dataHolder
                    .get(TBM_TAGGED).map(TBMTaggedData::new).orElse(null));
            return Optional.of(result);
        }

        @Override
        public Optional<TBMTaggedData> from(DataContainer container) {
            return container.getBoolean(TBM_TAGGED.getQuery())
                    .map(TBMTaggedData::new);
        }

        @Override
        public TBMTaggedData copy() {
            return new TBMTaggedData(this.getValue());
        }

        @Override
        public ImmutableTBMTaggedData asImmutable() {
            return ImmutableTBMTaggedData.of(this.getValue());
        }

    }

    public static final class ImmutableTBMTaggedData extends
            AbstractImmutableBooleanData<ImmutableTBMTaggedData, TBMTaggedData> {

        private static final ImmutableTBMTaggedData TRUE =
                new ImmutableTBMTaggedData(true);
        private static final ImmutableTBMTaggedData FALSE =
                new ImmutableTBMTaggedData(false);

        public static ImmutableTBMTaggedData of(boolean value) {
            return value ? TRUE : FALSE;
        }

        private ImmutableTBMTaggedData(boolean value) {
            super(value, TBM_TAGGED, false);
        }

        @Override
        public <E> Optional<ImmutableTBMTaggedData>
                with(Key<? extends BaseValue<E>> key, E value) {
            return null;
        }

        @Override
        public TBMTaggedData asMutable() {
            return new TBMTaggedData(this.value);
        }

    }

    public static final Key<Value<Boolean>> TBM_TAGGED = KeyFactory
            .makeSingleKey(Boolean.class, Value.class, DataQuery.of("tbmTag"));

    public static void registerKeyStuff() {
        Sponge.getDataManager().register(TBMTaggedData.class,
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
                        return new TBMTaggedData(false);
                    }

                    @Override
                    public Optional<TBMTaggedData>
                            createFrom(DataHolder dataHolder) {
                        return dataHolder.get(TBM_TAGGED)
                                .map(TBMTaggedData::new);
                    }

                });
    }

    private TBMKeys() {
    }

}
