package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcStatus;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

public class DynamicLB implements ILoadBalance {
    /**
     * 每隔一百毫秒调整一次
     */
    private static final int RECYCLE_PERIOD = 1000;
    /**
     * 上次调整时间
     */
    private static volatile long LASR_TIME = 0L;

    protected static class WeightedRoundRobin {
        private int weight;
        private AtomicLong current = new AtomicLong(0);
        /**
         * 如果长时间
         */
        private long lastUpdate;

        public int getWeight() {
            return weight;
        }

        public WeightedRoundRobin(int weight, Long current) {
            this.weight = weight;
            this.current.set(current);
        }

        public void setWeight(int weight) {
            this.weight = weight;
            current.set(0);
        }

        public long increaseCurrent() {
            return current.addAndGet(weight);
        }

        public void sel(int total) {
            current.addAndGet(-1 * total);
        }

        public void setCurrent(long current) {
            this.current.set(current);
        }

        public long getLastUpdate() {
            return lastUpdate;
        }

        public void setLastUpdate(long lastUpdate) {
            this.lastUpdate = lastUpdate;
        }

        @Override
        public String toString() {
            return "{" +
                    "weight=" + weight +
                    ", current=" + current +
                    '}';
        }
    }

    public static ConcurrentMap<String, DynamicLB.WeightedRoundRobin> methodWeightMap = new ConcurrentHashMap<>();


    @Override
    public <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        int wSize = methodWeightMap.size();
        int iSize = invokers.size();
        //如果权重信息没有加载完 随机
        if (wSize != iSize) {
            Invoker<T> invoker = invokers.get(ThreadLocalRandom.current().nextInt(invokers.size()));
            return invoker;
        }
        long now = System.currentTimeMillis();
        if (now - LASR_TIME > RECYCLE_PERIOD) {
            LASR_TIME = now;
            for (Invoker<T> invoker : invokers) {
                URL url1 = invoker.getUrl();
                RpcStatus status = RpcStatus.getStatus(url1);
                DynamicLB.WeightedRoundRobin roundRobin = methodWeightMap.get(url1.getIp() + url1.getPort());
                roundRobin.setWeight(roundRobin.getWeight() <= 0 ? 100 : (int) status.getSucceeded());
                roundRobin.setCurrent(0);
                RpcStatus.removeStatus(url1);
            }
//            System.out.println(methodWeightMap);
        }
        int totalWeight = 0;
        long maxCurrent = Long.MIN_VALUE;

        Invoker<T> selectedInvoker = null;
        DynamicLB.WeightedRoundRobin selectedWRR = null;

        //调整权重结束
        for (Invoker<T> invoker : invokers) {
            URL url1 = invoker.getUrl();
            String ip = url1.getIp();
            int port = url1.getPort();
            String key = ip + port;
            DynamicLB.WeightedRoundRobin weightedRoundRobin = methodWeightMap.get(key);
            int weight = methodWeightMap.get(key).getWeight();
            long cur = weightedRoundRobin.increaseCurrent();
            if (cur > maxCurrent) {
                maxCurrent = cur;
                selectedInvoker = invoker;
                selectedWRR = weightedRoundRobin;
            }
            totalWeight += weight;
        }
        if (selectedInvoker != null) {
            selectedWRR.sel(totalWeight);
            return selectedInvoker;
        }
        // should not happen here
        return invokers.get(ThreadLocalRandom.current().nextInt(iSize));
    }
}
