/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 03.07.2018 08:48:15
 */
package com.mepsan.marwiz.service.purchace.dao;

import com.mepsan.marwiz.general.model.log.SendPurchase;
import java.util.List;

public interface ISendPurchaseDao {

    public List<SendPurchase> findAll();

    public List<SendPurchase> findNotSendedAll();

    public SendPurchase findByInvoiceId(int invoiceId);

    public int updateSendPurchaseResult(SendPurchase sendPurchase);

}
