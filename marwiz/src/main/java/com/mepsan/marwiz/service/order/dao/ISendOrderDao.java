/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.order.dao;

import com.mepsan.marwiz.general.model.log.SendOrder;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface ISendOrderDao {
    
    public List<SendOrder> findAll();

    public List<SendOrder> findNotSendedAll();

    public SendOrder findByOrderId(int orderId);

    public int updateSendOrderResult(SendOrder sendOrder);
    
}
