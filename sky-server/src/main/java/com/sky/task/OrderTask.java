package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单超时处理
 */

@Component//注解
@Slf4j//日志
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;
    @Scheduled(cron = "0 * * * * ?") //定时任务
    public void processTimeoutOrder() {
        log.info("processTimeoutOrder");
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-15);
        List<Orders> byStatusAndOrderTimeLT = orderMapper.getByStatusAndOrderTimeLT(Orders.PENDING_PAYMENT, localDateTime);
        if(byStatusAndOrderTimeLT != null && byStatusAndOrderTimeLT.size() > 0){
            byStatusAndOrderTimeLT.forEach(orders -> {
                log.info("订单超时处理");
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时未支付");
                orderMapper.update(orders);
            });
        }
    }
    @Scheduled(cron = "0 0 1 * *  ?") //每天凌晨1点触发一次
    public void processDeliveryOrder() {
        LocalDateTime localDateTime = LocalDateTime.now().plusMinutes(-60);
        List<Orders> byStatusAndOrderTimeLT = orderMapper.getByStatusAndOrderTimeLT(Orders.DELIVERY_IN_PROGRESS, localDateTime);
        if(byStatusAndOrderTimeLT != null && byStatusAndOrderTimeLT.size() > 0){
            byStatusAndOrderTimeLT.forEach(orders -> {
                log.info("订单超时处理");
                orders.setStatus(Orders.CANCELLED);
                orders.setCancelReason("订单超时未派单");
                orderMapper.update(orders);
            });
        }
    }
}
