/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 23.03.2018 18:02:24 
 */
package com.mepsan.marwiz.service.sale.business;

import com.mepsan.marwiz.general.model.log.SendSale;
import java.util.List;

public interface ISendSaleService {
    public SendSale findBySaleId(int saleId);
    public boolean checkUser(String username, String password);
    public int updateSendSaleResult(SendSale sendSale);
    public void sendSaleToCenter(SendSale sendSale);
    public void sendSaleToCenterAsync(SendSale sendSale);
    public void sendSaleNotSendedToCenterAsync();
}
