package com.aliware.tianchi;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ClientStatus {
    private static final ConcurrentMap<String, ClientStatus> SERVICE_STATISTICS = new ConcurrentHashMap<>();

    final AtomicInteger failed = new AtomicInteger(0);
    final AtomicInteger activeCount = new AtomicInteger(0);
    final AtomicInteger rtt = new AtomicInteger(0);
//    final AtomicLong requestCount = new AtomicLong(0);
//    final AtomicLong responseCount = new AtomicLong(0);
//    private int port = 0;
//    private String ip = null;

    private ClientStatus(){
//        this.ip=ip;
//        this.port=port;
    }

    public static void requestCount(String key) {

        ClientStatus clientStatus = getStatus(key);
        clientStatus.activeCount.incrementAndGet();
//        clientStatus.requestCount.incrementAndGet();
    }

    public static void clear(String key){
        ClientStatus clientStatus = getStatus(key);
//        clientStatus.activeCount.set(0);
//        clientStatus.requestCount.set(0);
//        clientStatus.responseCount.set(0);
        clientStatus.failed.set(0);
    }
    public static ClientStatus getStatus(String key) {
        ClientStatus status = SERVICE_STATISTICS.get(key);
        if (status == null) {
            SERVICE_STATISTICS.putIfAbsent(key, new ClientStatus());
            status = SERVICE_STATISTICS.get(key);
        }
        return status;
    }

    public static void responseCount(String key,boolean fail,int rtt1) {
        ClientStatus clientStatus = getStatus(key);
        clientStatus.activeCount.decrementAndGet();
        clientStatus.rtt.set(rtt1);
//        clientStatus.responseCount.incrementAndGet();
        /*if(fail) {
            clientStatus.failed.incrementAndGet();
        }*/

    }


    public static ConcurrentMap<String, ClientStatus> getServiceStatistics() {
        return SERVICE_STATISTICS;
    }

    @Override
    public String toString() {
        return "{" +
                "failed=" + failed +
                ", activeCount=" + activeCount +
                ", rtt=" + rtt +
                '}';
    }
}
