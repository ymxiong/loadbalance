package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import java.util.*;
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
            Map<String, String> attachments = invocation.getAttachments();
            String value = UUID.randomUUID().toString();
            attachments.put("hello", value);
            com.aliware.tianchi.Constants.concurrentHashMap.put(value, new Date().getTime());
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
        Map<String, String> attachments = invocation.getAttachments();
        String value = attachments.get("hello");
        Long startTime = concurrentHashMap.get(value);
        Long endTime = new Date().getTime();
        if (port == 20880) {
            linkedHashMapSmall.put(value, endTime - startTime);
            System.out.println("small:" + (endTime - startTime));
            longAdderSmall.increment();
        } else if (port == 20870) {
            linkedHashMapMedium.put(value, endTime - startTime);
            System.out.println("medium:" + (endTime - startTime));
            longAdderMedium.increment();
        } else {
            linkedHashMapLarge.put(value, endTime - startTime);
            System.out.println("large:" + (endTime - startTime));
            longAdderLarge.increment();
        }

        return result;
    }
}
