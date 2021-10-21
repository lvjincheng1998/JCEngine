package pers.jc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JCCache {
    private final HashMap<String, CacheMapValue> cacheMap = new HashMap<>();
    private final ReentrantReadWriteLock cacheMapLock = new ReentrantReadWriteLock();

    private static final class SingletonHolder {
        private static final JCCache INSTANCE = new JCCache();
    }

    public static JCCache ins() {
        return SingletonHolder.INSTANCE;
    }

    private JCCache() {
        autoClear();
    }

    public void put(String key, Object value) {
        if (key == null || value == null) {
            JCLogger.errorStackTrace("JCCache Put Key Or Value Can Not Be Null");
            return;
        }
        cacheMapLock.writeLock().lock();
        cacheMap.put(key, new CacheMapValue(value, 0));
        cacheMapLock.writeLock().unlock();
    }

    public void put(String key, Object value, long timeMillis) {
        if (key == null || value == null) {
            JCLogger.errorStackTrace("JCCache Put Key Or Value Can Not Be Null");
            return;
        }
        cacheMapLock.writeLock().lock();
        cacheMap.put(key, new CacheMapValue(value, System.currentTimeMillis() + timeMillis));
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
                value = type.cast(cacheMapValue.getValue());
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
        if (isValid(cacheMapValue) && cacheMapValue.getValue().equals(value)) {
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
                value = type.cast(cacheMapValue.getValue());
            }
        } finally {
            cacheMapLock.readLock().unlock();
        }
        return value;
    }

    private boolean isValid(CacheMapValue cacheMapValue) {
        if (cacheMapValue != null && (cacheMapValue.timeout == 0 || System.currentTimeMillis() < cacheMapValue.timeout)) {
            return true;
        }
        return false;
    }

    private void autoClear() {
        Thread thread = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                cacheMapLock.writeLock().lock();
                ArrayList<String> timeoutKeys = new ArrayList();
                for (Map.Entry<String, CacheMapValue> entry : cacheMap.entrySet()) {
                    if (!isValid(entry.getValue())) {
                        timeoutKeys.add(entry.getKey());
                    }
                }
                for (String timeoutKey : timeoutKeys) {
                    cacheMap.remove(timeoutKey);
                }
                cacheMapLock.writeLock().unlock();
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    private class CacheMapValue {
        private Object value;
        private long timeout;

        public CacheMapValue(Object value, long timeout) {
            this.value = value;
            this.timeout = timeout;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }

        public long getTimeout() {
            return timeout;
        }

        public void setTimeout(long timeout) {
            this.timeout = timeout;
        }
    }
}
