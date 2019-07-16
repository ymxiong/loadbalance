package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import static com.aliware.tianchi.Constants.*;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestClientFilter.class);


    //long startTime = 0;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            //startTime = System.currentTimeMillis();
            URL url = invoker.getUrl();
            int port = url.getPort();
            if (port == 20880) {
                longAdderSmall.decrement();
//                LOGGER.info(new Date().getTime() + ":small:" + (com.aliware.tianchi.Constants.activeThreadCount.get("small") + ":" + com.aliware.tianchi.Constants.longAdderSmall.longValue()));
            } else if (port == 20870) {
                longAdderMedium.decrement();
//                LOGGER.info(new Date().getTime() + ":medium:" + com.aliware.tianchi.Constants.activeThreadCount.get("medium") + ":" + com.aliware.tianchi.Constants.longAdderMedium.longValue());
            } else {
                longAdderLarge.decrement();
//                LOGGER.info(new Date().getTime() + ":large:" + (com.aliware.tianchi.Constants.activeThreadCount.get("large") + ":" + com.aliware.tianchi.Constants.longAdderLarge.longValue()));
            }
            Map argument = (Map<String, Long>) invocation.getArguments()[0];
//            com.aliware.tianchi.Constants.concurrentHashMap.put(String.valueOf(argument.get("id")), new Date().getTime());
            Result result = invoker.invoke(invocation);
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        // long endTime = System.currentTimeMillis();
        //System.out.println( "request time : " +(endTime - startTime));
        URL url = invoker.getUrl();
        int port = url.getPort();
        Map argument = (Map<String, Long>) invocation.getArguments()[0];
        Long startTime = concurrentHashMap.get(String.valueOf(argument.get("id")));
        Long endTime = new Date().getTime();
        if (port == 20880) {
            linkedHashMapSmall.put(String.valueOf(argument.get("id")), endTime - startTime);
            System.out.println("small:" + (endTime - startTime));
            longAdderSmall.increment();
        } else if (port == 20870) {
            linkedHashMapMedium.put(String.valueOf(argument.get("id")), endTime - startTime);
            System.out.println("medium:" + (endTime - startTime));
            longAdderMedium.increment();
        } else {
            linkedHashMapLarge.put(String.valueOf(argument.get("id")), endTime - startTime);
            System.out.println("large:" + (endTime - startTime));
            longAdderLarge.increment();
        }

        return result;
    }
}
