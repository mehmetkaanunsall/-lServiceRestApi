/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.order.business;

import com.mepsan.marwiz.finance.invoice.dao.RelatedRecord;
import com.mepsan.marwiz.finance.order.dao.OrderRelatedRecord;
import com.mepsan.marwiz.general.model.finance.Order;
import java.util.List;

/**
 *
 * @author ebubekir.buker
 */
public interface IOrderRelatedRecordService 
{
     List<OrderRelatedRecord>listOfOrderRelatedRecords(Order order);
}
