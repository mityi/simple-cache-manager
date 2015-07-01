package scm;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SimpleCacheManager {
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final Map<String, Map.Entry<Future, Long>> cacheData = new ConcurrentHashMap();

    public Future get(final String key, final long timeLife, final Method method, final Object[] args) {
        Map.Entry<Future, Long> sr = cacheData.get(key);
        if (sr != null && sr.getValue() > System.currentTimeMillis()) {
            return sr.getKey();
        }
        Future val = executor.submit(new Callable() {
            public Object call() throws Exception {
                return method.invoke(args);
            }
        });
        cacheData.put(key, new AbstractMap.SimpleEntry(val, timeLife + System.currentTimeMillis()));
        return val;
    }

    //TODO add evict old date ba scheduler 
    
    public void evict(Object key) {
        cacheData.clear();
    }
}
