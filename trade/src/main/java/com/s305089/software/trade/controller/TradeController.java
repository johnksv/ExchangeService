package com.s305089.software.trade.controller;

import com.s305089.software.trade.dao.OrderDao;
import com.s305089.software.trade.dao.PayRecordDao;
import com.s305089.software.trade.logging.OrderLogger;
import com.s305089.software.trade.logic.NetworkUtil;
import com.s305089.software.trade.logic.TradeLogic;
import com.s305089.software.trade.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@CrossOrigin(origins = "http://localhost:3000")
@RestController
public class TradeController {

    @Autowired
    private OrderDao orderDao;
    @Autowired
    private PayRecordDao payRecordDao;

    @GetMapping(value = "/payrecords")
    public Iterable<PayRecord> getpayrecord(){
        return payRecordDao.findAll();
    }

    @GetMapping()
    public Iterable<Order> getOrders() {
        return orderDao.findAll();
    }

    @GetMapping(value = "/{market}")
    public Iterable<Order> getOrders(@PathVariable Market market) {
        return orderDao.findByMarket(market);
    }
    @GetMapping(value = "/{market}/{transactionType}")
    public Iterable<Order> getOrders(@PathVariable Market market, @PathVariable TransactionType transactionType) {
        return orderDao.findByMarketAndTransactionType(market, transactionType);
    }


    @PostMapping
    public ResponseEntity makeOrder(@RequestBody OrderDTO orderDTO) {

        Order order = orderDTO.getOrder();
        order.setTimestamp(new Date());


        boolean fundsOK = NetworkUtil.checkFunds(orderDTO.getUsername(), orderDTO.getPassword(), order);
        if (!fundsOK) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        boolean reserveOK = NetworkUtil.sendReserveOrder(order.getUserID(), order.getMarket(), order.getTotal(), order.getTransactionType());
        if(!reserveOK) return   ResponseEntity.status(HttpStatus.FORBIDDEN).build();;
        order = orderDao.save(order);

        //Preform transaction to match with bids (buys) and asks (sells)
        Transaction transaction = TradeLogic.performTransaction(order, orderDao.findByActiveTrue());
        List<PayRecord> payRecords = transaction.generatePayrecords();
        boolean tradeOK = NetworkUtil.sendPayRecordsToUserService(payRecords);

        if (tradeOK) {
            List<Order> orders = transaction.getAllOrders();
            orderDao.saveAll(orders);

            payRecordDao.saveAll(payRecords);

            OrderLogger.logToLogService(order);
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.notFound().build();
    }


    @GetMapping(value = "/markets")
    public List<Market> getMarkets() {
        return Arrays.asList(Market.values());
    }

}
