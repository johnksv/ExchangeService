package com.s305089.software.trade.logic;

import com.s305089.software.trade.model.Order;
import com.s305089.software.trade.model.Transaction;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.s305089.software.trade.model.TransactionType.BUY;

public class TradeLogic {

    /**
     * Tries to fulfill the order given, with those orders who already are active. The users accounts will be notified and charged/payed accordingly.
     *
     * @param orderToFullfill The order we want to trade
     * @param allActiveOrders All current active orders (most likely from the database)
     * @return A list of orders with updated values ready to be saved to database. T
     */
    public static Transaction performTransaction(Order orderToFullfill, List<Order> allActiveOrders) {
        List<Order> ordersThatCanBeTradedWith = allActiveOrders
                .stream()
                .filter(order -> order.getMarket().equals(orderToFullfill.getMarket()))
                .filter(order -> order.getTransactionType().isOpposit(orderToFullfill.getTransactionType()))
                .filter(order -> order.getPrice().compareTo(orderToFullfill.getPrice()) == 0)
                .filter(order -> order.getRemainingAmount().compareTo(new BigDecimal(0)) > 0)
                .filter(order -> !order.getUserID().equals(orderToFullfill.getUserID()))
                .collect(Collectors.toList());

        return makeTransaction(orderToFullfill, ordersThatCanBeTradedWith);

    }

    /**
     * Construct a transaction that contains orders it can be traded with
     *
     * @param orderToFullfill
     * @param ordersThatCanBeTradedWith
     * @return A transaction ready to fulfill.
     */
    private static Transaction makeTransaction(Order orderToFullfill, List<Order> ordersThatCanBeTradedWith) {
        Transaction transaction;

        Optional<Order> matchingOrder = ordersThatCanBeTradedWith
                .stream()
                .filter(order ->
                        //If an order matches the amount exact
                        order.getRemainingAmount().compareTo(orderToFullfill.getAmount()) == 0)
                .findAny();
        if (matchingOrder.isPresent()) {
            Order order = matchingOrder.get();
            transaction = new Transaction(orderToFullfill, order);
        } else {
            List<Order> ordersToMakeTransaction = new ArrayList<>();
            BigDecimal amount = new BigDecimal(0);
            //NB: We might add orders so we get above the amount to fulfill, this is OK and taken care of when we fulfill the transaction.
            for (int i = 0; amount.compareTo(orderToFullfill.getAmount()) < 0 && i < ordersThatCanBeTradedWith.size(); i++) {
                Order order = ordersThatCanBeTradedWith.get(i);
                amount = amount.add(order.getRemainingAmount());
                ordersToMakeTransaction.add(order);
            }
            transaction = new Transaction(orderToFullfill, ordersToMakeTransaction);
        }

        return transaction;
    }

}
