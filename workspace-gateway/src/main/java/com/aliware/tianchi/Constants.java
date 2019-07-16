package com.aliware.tianchi;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

public class Constants {
//    public static final ThreadLocal<Integer>  threadLocal = new ThreadLocal();
/*    public static  Integer threadSmall = 0;
    public static  Integer threadMedium = 0;
    public static  Integer threadLarge = 0;*/

    public static Map<String, Integer> activeThreadCount = new HashMap<>();
    public static LongAdder longAdderLarge = new LongAdder();
    public static LongAdder longAdderMedium = new LongAdder();
    public static LongAdder longAdderSmall = new LongAdder();
    public static ConcurrentHashMap<String, Long> concurrentHashMap = new ConcurrentHashMap();
    public static Long lastLarge = 0l;
    public static Long lastMedium = 0l;
    public static Long lastSmall = 0l;

    public static AtomicLong smallSum = new AtomicLong();
    public static final int capacity = 10;
    public static LinkedHashMap<String, Long> linkedHashMapSmall = new LinkedHashMap<String, Long>(10) {
        @Override
        public Long put(String key, Long value) {
            smallSum.addAndGet(value);
            return super.put(key, value);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            smallSum.addAndGet(-eldest.getValue());
            return size() > capacity;
        }
    };
    public static AtomicLong mediumSum = new AtomicLong();
    public static LinkedHashMap<String, Long> linkedHashMapMedium = new LinkedHashMap<String, Long>(10) {
        @Override
        public Long put(String key, Long value) {
            mediumSum.addAndGet(value);
            return super.put(key, value);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            mediumSum.addAndGet(-eldest.getValue());
            return size() > capacity;
        }
    };
    public static AtomicLong largeSum = new AtomicLong();
    public static LinkedHashMap<String, Long> linkedHashMapLarge = new LinkedHashMap<String, Long>(10) {
        @Override
        public Long put(String key, Long value) {
            largeSum.addAndGet(value);
            return super.put(key, value);
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
            largeSum.addAndGet(-eldest.getValue());
            return size() > capacity;
        }
    };

    static {
        longAdderLarge.add(650);
        longAdderMedium.add(450);
        longAdderSmall.add(200);
        activeThreadCount.put("small", 200);
        activeThreadCount.put("medium", 450);
        activeThreadCount.put("large", 650);
    }

}
