package com.driver;

import org.springframework.stereotype.Repository;
import org.w3c.dom.ls.LSException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
public class OrderRepository {
    HashMap<String, Order> orderHashMap;
    HashMap<String, DeliveryPartner> deliveryPartnerHashMap;
    HashMap<String, String> orderPartnerId;  //<orderId,deliveryPartnerId>.
    HashMap<String, List<String>> partnerOrder; //<partnerId, list of Orders>.

    public OrderRepository(){
        orderHashMap = new HashMap<>();
        deliveryPartnerHashMap = new HashMap<>();
        orderPartnerId = new HashMap<>();
        partnerOrder = new HashMap<>();
    }

    public void addOrder(Order order){
        orderHashMap.put(order.getId(), order);
    }

    public void addPartner(String partnerId){
        DeliveryPartner d = new DeliveryPartner(partnerId);
        deliveryPartnerHashMap.put(partnerId,   d);
    }

    public void addOrderPartnerPair(String orderId, String partnerId){
        if (orderHashMap.containsKey(orderId) && deliveryPartnerHashMap.containsKey(partnerId)) {
            orderPartnerId.put(orderId, partnerId);
            List<String> currOrders = new ArrayList<>();
            if(partnerOrder.containsKey(partnerId)) currOrders = partnerOrder.get(partnerId);

            currOrders.add(orderId);
            partnerOrder.put(partnerId, currOrders);

            //increase the no of orders of partner.
            DeliveryPartner d = deliveryPartnerHashMap.get(partnerId);
            d.setNumberOfOrders(d.getNumberOfOrders()+1);
        }
    }

    public Order getOrderById(String orderId){
        return orderHashMap.get(orderId);
    }

    public DeliveryPartner getPartnerById(String partnerId){
        return deliveryPartnerHashMap.get(partnerId);
    }

    public int getOrderCountByPartnerId(String partnerId){
        return partnerOrder.get(partnerId).size();
//        DeliveryPartner d = deliveryPartnerHashMap.get(partnerId);
//        return d.getNumberOfOrders();
    }

    public List<String> getOrdersByPartnerId(String partnerId){
        return partnerOrder.get(partnerId);
    }

    public List<String> getAllOrders(){
        List<String> orders = new ArrayList<>();
        for(String s : orderHashMap.keySet())
            orders.add(s);

        return orders;
    }

    public int getCountOfUnassignedOrders(){
        return orderHashMap.size() - orderPartnerId.size();
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(int time, String partnerId){
        int count = 0;
        List<String> orders = partnerOrder.get(partnerId);
        for(String s : orders){
            int deliveryTime = orderHashMap.get(s).getDeliveryTime();
            if(deliveryTime > time) count++;
        }

        return count;
    }

    public int getLastDeliveryTimeByPartnerId(String partnerId){
        int maxTime = 0;
        List<String> orderes = partnerOrder.get(partnerId);
        for(String s : orderes){
            int currTime = orderHashMap.get(s).getDeliveryTime();
            maxTime = Math.max(maxTime, currTime);
        }

        return  maxTime;
    }

    public void deletePartnerById(String partnerId){
        deliveryPartnerHashMap.remove(partnerId);

        List<String> orders = partnerOrder.get(partnerId);
        partnerOrder.remove(partnerId);

        for(String s : orders){
            orderPartnerId.remove(s);
        }
    }

    public void deleteOrderById(String orderId){
        orderHashMap.remove(orderId);

        String partnerId = orderPartnerId.get(orderId);
        orderPartnerId.remove(orderId);

        partnerOrder.get(partnerId).remove(orderId);

        deliveryPartnerHashMap.get(partnerId).setNumberOfOrders(partnerOrder.get(partnerId).size());
    }
}
