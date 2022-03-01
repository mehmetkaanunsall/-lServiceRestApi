/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.business;

import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.List;
import java.util.Map;

/**
 *
 * @author esra.cabuk
 */
public interface IOrderItemService extends ICrudService<OrderItem>, ILazyGridService<OrderItem>{
  
    public List<OrderItem> findAllAccordingToOrder(int processType, Order order);
    
    public int createAll(List<OrderItem> list, Order obj);
    
    public String jsonArrayOrderItems(List<OrderItem> list);
    
    public int updateAll(List<OrderItem> list, Order obj);
    
    public List<OrderItem> listOrderItemForCreateInvoice(List<Order> orderList, List<OrderItem> orderItemList);
    
    public List<OrderItem> findAllNotLazy(String where);
    
    public int saveDescription(OrderItem orderItem);
    
    public List<OrderItem> findAllAccordingToStock(int processType, OrderItem orderItem);
}
