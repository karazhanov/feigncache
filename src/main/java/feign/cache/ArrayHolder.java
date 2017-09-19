package feign.cache;

import java.util.Arrays;

/**
 * @author karazhanov on 18.09.17.
 */
class ArrayHolder<T> {
    private final T[] array;
    private final int hashCode;

    @SafeVarargs
    ArrayHolder(T... ts) {
        array = ts;
        hashCode = Arrays.hashCode(array);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public boolean equals(Object other) {
        if (array == other) {
            return true;
        }
        if (!(other instanceof ArrayHolder)) {
            return false;
        }
        return Arrays.equals(array, ((ArrayHolder) other).array);
    }
}
