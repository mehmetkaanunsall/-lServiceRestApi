/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.service.order.business;

import com.mepsan.marwiz.service.order.dao.ICreateOrderDao;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class CreateOrderService implements ICreateOrderService{

    @Autowired ICreateOrderDao createOrderDao;
    
    @Override
    public int createOrderJob(int branch_id) {
        return createOrderDao.createOrderJob(branch_id);
    }
    
}
