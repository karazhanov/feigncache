package feign.cache;

/**
 * @author karazhanov on 18.09.17.
 */
class DataHolder<T> {
    private long validUntil;
    private T data;

    DataHolder(long cacheTime, T data) {
        this.validUntil = cacheTime + System.currentTimeMillis();
        this.data = data;
    }

    boolean isValid() {
        return System.currentTimeMillis() < validUntil;
    }

    T getData() {
        return data;
    }
}
