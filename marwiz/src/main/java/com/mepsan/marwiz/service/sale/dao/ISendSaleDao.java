/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 23.03.2018 15:54:14
 */
package com.mepsan.marwiz.service.sale.dao;

import com.mepsan.marwiz.general.model.log.SendSale;
import java.util.List;

public interface ISendSaleDao {

    public List<SendSale> findAll();

    public List<SendSale> findNotSendedAll();

    public SendSale findBySaleId(int saleId);

    public int updateSendSaleResult(SendSale sendSale);
    
    
}
