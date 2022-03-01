/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.finance.OrderItem;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;

/**
 *
 * @author esra.cabuk
 */
public interface IOrderItemDao extends ICrud<OrderItem>, ILazyGrid<OrderItem>{
   
    public List<OrderItem> findAllAccordingToOrder(int processType, Order order);
     
    public int createAll(Order obj); 

    public int updateAll(Order obj);     
    
    public List<OrderItem> listOrderItemForCreateInvoice(String where);
    
    public List<OrderItem> findAllNotLazy(String where);
    
    public int saveDescription(OrderItem orderItem);
    
    public List<OrderItem> findAllAccordingToStock(int processType, OrderItem orderItem);
}
