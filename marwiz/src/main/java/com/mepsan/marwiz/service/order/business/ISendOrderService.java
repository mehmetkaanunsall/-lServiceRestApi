/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.order.business;

import com.mepsan.marwiz.general.model.log.SendOrder;

/**
 *
 * @author esra.cabuk
 */
public interface ISendOrderService {
    
    public SendOrder findByOrderId(int orderId);

    public int updateSendOrderResult(SendOrder sendOrder);

    public void sendOrderToCenter(SendOrder sendOrder);

    public void sendOrderToCenterAsync(SendOrder sendOrder);

    public void sendOrderNotSendedToCenterAsync();
    
}
