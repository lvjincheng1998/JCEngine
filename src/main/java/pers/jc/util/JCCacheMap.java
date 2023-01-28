package pers.jc.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;

import java.io.File;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class JCCacheMap {
    private Map<String, CacheMapValue> cacheMap = new HashMap<>();
    private final ReentrantReadWriteLock cacheMapLock = new ReentrantReadWriteLock();

    public JCCacheMap() {
        autoClear(this);
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

    private static final List<JCCacheMap> autoClearMapList = new LinkedList<>();
    private static boolean autoClearStarted = false;
    private static final long autoClearInterval = 10 * 1000;

    private static synchronized void autoClear(JCCacheMap map) {
        autoClearMapList.add(map);
        if (!autoClearStarted) {
            autoClearStarted = true;
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    autoClearHandle();
                }
            }, autoClearInterval, autoClearInterval);
        }
    }

    public static synchronized void autoClearHandle() {
        for (JCCacheMap map : autoClearMapList) {
            map.cacheMapLock.writeLock().lock();
            Iterator<Map.Entry<String, CacheMapValue>> iterator = map.cacheMap.entrySet().iterator();
            Map.Entry<String, CacheMapValue> entry;
            while (iterator.hasNext()) {
                entry = iterator.next();
                if (!map.isValid(entry.getValue())) iterator.remove();
            }
            map.cacheMapLock.writeLock().unlock();
        }
    }

    public static class CacheMapValue {
        @JSONField
        public Object value;
        @JSONField
        public long timeout;

        public CacheMapValue() {}

        public CacheMapValue(Object value, long timeout) {
            this.value = value;
            this.timeout = timeout;
        }
    }

    public void saveTo(String dirPath, String fileNameNoSuffix) {
        File dir = new File(dirPath);
        if (!dir.exists()) dir.mkdirs();
        try {
            cacheMapLock.readLock().lock();
            File file = new File(dirPath + File.separator + fileNameNoSuffix + ".json");
            if (!file.exists()) file.createNewFile();
            JCFileTool.writeStr(file, JSON.toJSONString(cacheMap));
        } catch (Exception e) { e.printStackTrace(); }
        finally {
            cacheMapLock.readLock().unlock();
        }
    }

    public void loadFrom(String dirPath, String fileNameNoSuffix) {
        File file = new File(dirPath + File.separator + fileNameNoSuffix + ".json");
        if (!file.exists()) return;
        try {
            cacheMapLock.writeLock().lock();
            JSONObject jsonMap = JSON.parseObject(JCFileTool.readStr(file));
            for (Map.Entry<String, Object> entry : jsonMap.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (value instanceof JSONObject) {
                    CacheMapValue cacheMapValue = ((JSONObject) value).toJavaObject(CacheMapValue.class);
                    cacheMap.put(key, cacheMapValue);
                }
            }
        } catch (Exception e) { e.printStackTrace(); }
        finally {
            cacheMapLock.writeLock().unlock();
        }
    }
}
