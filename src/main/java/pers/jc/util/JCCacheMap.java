package pers.jc.util;

import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JCCacheMap {
    private final Map<String, CacheMapValue> cacheMap = new HashMap<>();
    private final ReentrantReadWriteLock cacheMapLock = new ReentrantReadWriteLock();

    private static final class SingletonHolder {
        private static final JCCacheMap INSTANCE = new JCCacheMap();
    }

    public static JCCacheMap ins() {
        return SingletonHolder.INSTANCE;
    }

    private JCCacheMap() {
        autoClear();
    }

    public void put(String key, Object value) {
        if (key == null || value == null) {
            JCLogger.errorStackTrace("JCCache Put Key Or Value Can Not Be Null");
            return;
        }
        cacheMapLock.writeLock().lock();
        cacheMap.put(key, new CacheMapValue(value, -1));
        cacheMapLock.writeLock().unlock();
    }

    public void put(String key, Object value, long timeout) {
        if (key == null || value == null) {
            JCLogger.errorStackTrace("JCCache Put Key Or Value Can Not Be Null");
            return;
        }
        cacheMapLock.writeLock().lock();
        cacheMap.put(key, new CacheMapValue(value, timeout));
        cacheMapLock.writeLock().unlock();
    }

    public Object remove(String key) {
        return remove(key, Object.class);
    }

    public <T> T remove(String key, Class<T> type) {
        T value = null;
        try {
            cacheMapLock.writeLock().lock();
            CacheMapValue cacheMapValue = cacheMap.remove(key);
            if (isValid(cacheMapValue)) {
                value = type.cast(cacheMapValue.value);
            }
        } finally {
            cacheMapLock.writeLock().unlock();
        }
        return value;
    }

    public boolean remove(String key, Object value) {
        boolean result = false;
        cacheMapLock.writeLock().lock();
        CacheMapValue cacheMapValue = cacheMap.get(key);
        if (isValid(cacheMapValue) && cacheMapValue.value.equals(value)) {
            cacheMap.remove(key);
            result = true;
        }
        cacheMapLock.writeLock().unlock();
        return result;
    }

    public Object get(String key) {
        return get(key, Object.class);
    }

    public <T> T get(String key, Class<T> type) {
        T value = null;
        try {
            cacheMapLock.readLock().lock();
            CacheMapValue cacheMapValue = cacheMap.get(key);
            if (isValid(cacheMapValue)) {
                value = type.cast(cacheMapValue.value);
            }
        } finally {
            cacheMapLock.readLock().unlock();
        }
        return value;
    }

    public int size() {
        cacheMapLock.readLock().lock();
        int size = 0;
        for (CacheMapValue value : cacheMap.values()) {
            if (isValid(value)) size++;
        }
        cacheMapLock.readLock().unlock();
        return size;
    }

    private boolean isValid(CacheMapValue cacheMapValue) {
        return cacheMapValue != null && (cacheMapValue.timeout == -1 || System.currentTimeMillis() < cacheMapValue.timeout);
    }

    private void autoClear() {
        long interval = 10 * 1000;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                cacheMapLock.writeLock().lock();
                Iterator<Map.Entry<String, CacheMapValue>> iterator = cacheMap.entrySet().iterator();
                Map.Entry<String, CacheMapValue> entry;
                while (iterator.hasNext()) {
                    entry = iterator.next();
                    if (!isValid(entry.getValue())) {
                        iterator.remove();
                    }
                }
                cacheMapLock.writeLock().unlock();
            }
        }, interval, interval);
    }

    private static class CacheMapValue {
        Object value;
        long timeout;

        CacheMapValue(Object value, long timeout) {
            this.value = value;
            this.timeout = timeout;
        }
    }
}
