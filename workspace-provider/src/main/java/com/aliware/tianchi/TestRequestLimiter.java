package com.aliware.tianchi;

//import org.apache.dubbo.common.logger.Logger;
//import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.transport.RequestLimiter;

//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.Date;

/**
 * @author daofeng.xjf
 *
 * 服务端限流
 * 可选接口
 * 在提交给后端线程池之前的扩展，可以用于服务端控制拒绝请求
 */
public class TestRequestLimiter implements RequestLimiter {

//    private static Logger LOGGER = LoggerFactory.getLogger(TestRequestLimiter.class);
    /**
     * @param request 服务请求
     * @param activeTaskCount 服务端对应线程池的活跃线程数
     * @return  false 不提交给服务端业务线程池直接返回，客户端可以在 Filter 中捕获 RpcException
     *          true 不限流
     */
    @Override
    public boolean tryAcquire(Request request, int activeTaskCount) {
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss:SSS");
//        LocalDateTime dateTime = LocalDateTime.now();
//        String formattedDateTime = dateTime.format(formatter); // "1986-04-08 12:30"
//        LOGGER.info("时间：｛"+formattedDateTime+"｝，活跃数：｛"+activeTaskCount+"｝");
        return true;
    }

}
