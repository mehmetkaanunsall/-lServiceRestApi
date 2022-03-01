/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.dao;

import com.mepsan.marwiz.general.model.finance.Order;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.pattern.ICrud;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import javax.sql.DataSource;

/**
 *
 * @author esra.cabuk
 */
public interface IOrderDao extends ICrud<Order>, ILazyGrid<Order>{
    
    public String exportData(Order order);

    public DataSource getDatasource();
    
    public int updateStatus(Order order);
    
    public int sendOrderCenter(Order order);
    
    public int delete(Order order);
    
     public CheckDelete testBeforeDelete(Order order);
    
}
