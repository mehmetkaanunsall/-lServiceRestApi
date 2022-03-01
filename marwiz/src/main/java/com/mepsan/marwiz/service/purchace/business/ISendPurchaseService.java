/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 03.07.2018 08:55:03
 */
package com.mepsan.marwiz.service.purchace.business;

import com.mepsan.marwiz.general.model.log.SendPurchase;

public interface ISendPurchaseService {

    public SendPurchase findByInvoiceId(int invoiceId);

    public int updateSendPurchaseResult(SendPurchase sendPurchase);

    public void sendPurchaseToCenter(SendPurchase sendPurchase);

    public void sendPurchaseToCenterAsync(SendPurchase sendPurchase);

    public void sendPurchaseNotSendedToCenterAsync();
}
