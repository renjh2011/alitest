package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

//import org.apache.dubbo.common.logger.Logger;
//import org.apache.dubbo.common.logger.LoggerFactory;
//import org.apache.dubbo.rpc.RpcStatus;
//import java.util.ArrayList;

/**
 * @author daofeng.xjf
 * <p>
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {
    ILoadBalance loadBalance = new DynamicLB();
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        return loadBalance.doSelect(invokers, url, invocation);
    }


}
