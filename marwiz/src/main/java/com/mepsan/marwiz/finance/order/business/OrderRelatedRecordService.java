/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.business;

import com.mepsan.marwiz.finance.order.dao.IOrderRelatedRecordDao;
import com.mepsan.marwiz.finance.order.dao.OrderRelatedRecord;
import com.mepsan.marwiz.general.model.finance.Order;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author ebubekir.buker
 */
public class OrderRelatedRecordService implements IOrderRelatedRecordService {
   
    @Autowired
    private IOrderRelatedRecordDao orderRelatedRecordDao;

    public void setOrderRelatedRecordDao(IOrderRelatedRecordDao orderRelatedRecordDao) {
        this.orderRelatedRecordDao = orderRelatedRecordDao;
    }

    @Override
    public List<OrderRelatedRecord> listOfOrderRelatedRecords(Order order) {
                return orderRelatedRecordDao.listOfOrderRelatedRecords(order);

    }
}
