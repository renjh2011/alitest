package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * @author daofeng.xjf
 *
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcStatus.beginCount(invoker.getUrl(),invocation.getMethodName());
        try{
            invocation.getAttachments().put("start",System.currentTimeMillis()+"");
            Result result = invoker.invoke(invocation);
            return result;
        }catch (Exception e){
            RpcStatus.endCount(invoker.getUrl(),invocation.getMethodName(),200L,false);
            throw e;
        }
    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        long elapsed = System.currentTimeMillis()-Long.parseLong(invocation.getAttachments().get("start"));
        String ip = invoker.getUrl().getIp();
        int port = invoker.getUrl().getPort();
        boolean succeeded = true;
        //初始化每个provider对应的线程池
        if(!result.hasException() && DynamicLB.methodWeightMap.get(ip+port)==null){
            String maxThreadPool = result.getAttachment(ip+port+"maxPool");
            DynamicLB.methodWeightMap.put(ip + port,new DynamicLB.WeightedRoundRobin(Integer.parseInt(maxThreadPool),0L));
        }
        if(result.hasException()){
            elapsed=200L;
            succeeded=false;
        }
        RpcStatus.endCount(invoker.getUrl(),invocation.getMethodName(),elapsed,succeeded);
        return result;
    }
}
