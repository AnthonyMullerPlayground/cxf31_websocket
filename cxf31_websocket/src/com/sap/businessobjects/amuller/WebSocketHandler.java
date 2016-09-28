package com.sap.businessobjects.amuller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class WebSocketHandler {

    private static final int MAX_ERROR_COUNT = 5;
    
    private static ExecutorService executor = Executors.newSingleThreadExecutor();
    
	private Map<String, WriterHolder<OutputStream>> monitors = new HashMap<String, WriterHolder<OutputStream>>();
	
	private static class WriterHolder<T> {
        private final T value;
        private final int max; 
        private final AtomicInteger errorCount;

        private WriterHolder(T object, int max) {
            this.value = object;
            this.max = max;
            this.errorCount = new AtomicInteger();
        }

        public T getValue() {
            return value;
        }

        public int get() {
            return errorCount.get();
        }
        public boolean increment() {
            return max < errorCount.getAndIncrement();
        }

        public void reset() {
            errorCount.getAndSet(0);
        }
    }
	
	public void addListener(String listenerId, OutputStream listener) {
		monitors.put(listenerId, new WriterHolder<OutputStream>(listener, MAX_ERROR_COUNT));
	}
	
	public void sendEvent(final String msg) {
        executor.execute(new Runnable() {
            public void run() {
                try {
                    String t = msg;
                    for (Iterator<WriterHolder<OutputStream>> it = monitors.values().iterator(); it.hasNext();) {
                        WriterHolder<OutputStream> wh = it.next();
                        try {
                            wh.getValue().write(t.getBytes());
                            wh.getValue().flush();
                            wh.reset();
                        } catch (IOException e) {
                            System.out.println("----error writing to " + wh.getValue() + " " + wh.get());
                            if (wh.increment()) {
                                // the max error count reached; purging the output resource
                                e.printStackTrace();
                                try {
                                    wh.getValue().close();
                                } catch (IOException e2) {
                                    // ignore;
                                }
                                it.remove();
                                System.out.println("----purged " + wh.getValue());
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
