/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.order.dao.IOrderItemDao;
import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class OrderItemService implements IOrderItemService {

    @Autowired
    private IOrderItemDao orderItemDao;

    public void setOrderItemDao(IOrderItemDao orderItemDao) {
        this.orderItemDao = orderItemDao;
    }

    @Override
    public int create(OrderItem obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(OrderItem obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<OrderItem> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where) {
        return orderItemDao.findAll(first, pageSize, sortField, sortOrder, filters, where);
    }

    @Override
    public int count(String where) {
        return orderItemDao.count(where);
    }

    @Override
    public List<OrderItem> findAllAccordingToOrder(int processType, Order order) {
        return orderItemDao.findAllAccordingToOrder(processType, order);
    }

    @Override
    public int createAll(List<OrderItem> list, Order obj) {
        obj.setJsonItems(jsonArrayOrderItems(list));
        return orderItemDao.createAll(obj);
    }

    @Override
    public String jsonArrayOrderItems(List<OrderItem> list) {
        JsonArray jsonArray = new JsonArray();
        for (OrderItem obj : list) {

            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("id", obj.getId());
            jsonObject.addProperty("stock_id", obj.getStock().getId());
            jsonObject.addProperty("unit_id", obj.getUnit().getId());
            jsonObject.addProperty("boxquantity", obj.getBoxQuantity());
            jsonObject.addProperty("shelfquantity", obj.getShelfQuantity());
            jsonObject.addProperty("minfactorvalue", obj.getMinFactorValue());
            jsonObject.addProperty("maxfactorvalue", obj.getMaxFactorValue());
            jsonObject.addProperty("warehousestockdivisorvalue", obj.getWarehouseStockDivisorValue());
            jsonObject.addProperty("warehousequantity", obj.getWarehouseQuantity());
            jsonObject.addProperty("twomonthsale", obj.getLastTwoMonthsSales());
            jsonObject.addProperty("twomonthsaleactiveday", obj.getTwoMonthSaleActiveDay());
            jsonObject.addProperty("minimumquantity", obj.getMinQuantity());
            jsonObject.addProperty("maximumquantity", obj.getMaxQuantity());
            jsonObject.addProperty("quantity", obj.getQuantity());
            jsonObject.addProperty("recommendedprice", obj.getRecommendedPrice());
            jsonObject.addProperty("currency_id", obj.getCurrency().getId());
            jsonObject.addProperty("stockenoughday", obj.getStockEnoughDay());
            jsonArray.add(jsonObject);
        }

//        System.out.println("---order---json---" + jsonArray.toString());
        return jsonArray.toString();
    }

    @Override
    public int updateAll(List<OrderItem> list, Order obj) {
        obj.setJsonItems(jsonArrayOrderItems(list));
        return orderItemDao.updateAll(obj);
    }

    @Override
    public List<OrderItem> listOrderItemForCreateInvoice(List<Order> orderList, List<OrderItem> orderItemList) {
        String where = "";
        if (!orderList.isEmpty()) {
            String ids = "";
            for (Order order : orderList) {
                ids = ids + order.getId() + ",";
            }

            if (!ids.equals("")) {
                ids = ids.substring(0, ids.length() - 1);
                where = where + "and odi.order_id in (" + ids + ")";
            }

        } else if (!orderItemList.isEmpty()) {
            String ids = "";
            for (OrderItem orderItem : orderItemList) {
                ids = ids + orderItem.getId() + ",";
            }

            if (!ids.equals("")) {
                ids = ids.substring(0, ids.length() - 1);
                where = where + "and odi.id in (" + ids + ")";
            }
        }

        return orderItemDao.listOrderItemForCreateInvoice(where);
    }

    @Override
    public List<OrderItem> findAllNotLazy(String where) {

        where = where + " and od.status_id=59 ";
        return orderItemDao.findAllNotLazy(where);
    }

    @Override
    public int saveDescription(OrderItem orderItem) {
        return orderItemDao.saveDescription(orderItem);
    }

    @Override
    public List<OrderItem> findAllAccordingToStock(int processType, OrderItem orderItem) {
        return orderItemDao.findAllAccordingToStock(processType, orderItem);
    }

}
