package me.kenzierocks.plugins.tbm.spongeabc;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.service.persistence.InvalidDataException;

@SuppressWarnings("unchecked")
public class DataUtil {

    public static DataView checkDataExists(final DataView dataView,
            final DataQuery query) throws InvalidDataException {
        if (!checkNotNull(dataView).contains(checkNotNull(query))) {
            throw new InvalidDataException(
                    "Missing data for query: " + query.asString('.'));
        } else {
            return dataView;
        }
    }

    public static <T> T getData(final DataView dataView,
            final Key<? extends BaseValue<T>> key) throws InvalidDataException {
        checkDataExists(dataView, checkNotNull(key).getQuery());
        final Object object = dataView.get(key.getQuery()).get();
        return (T) object;
    }

    public static <T> T getData(final DataView dataView, final Key<?> key,
            Class<T> clazz) throws InvalidDataException {
        checkDataExists(dataView, checkNotNull(key).getQuery());
        final Object object = dataView.get(key.getQuery()).get();
        if (clazz.isInstance(object)) {
            return (T) object;
        } else {
            throw new InvalidDataException(
                    "Could not cast to the correct class type!");
        }
    }

    public static <T> T getData(final DataView dataView, final DataQuery query,
            Class<T> data) throws InvalidDataException {
        checkDataExists(dataView, query);
        final Object object = dataView.get(query).get();
        if (data.isInstance(object)) {
            return (T) object;
        } else {
            throw new InvalidDataException("Data does not match!");
        }
    }

}