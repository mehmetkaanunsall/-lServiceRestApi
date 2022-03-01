/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 25.06.2018 08:33:06
 */
package com.mepsan.marwiz.finance.invoice.business;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoicePayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.util.List;

public interface IInvoicePaymentService {

    public int create(InvoicePayment obj);
    
     public int update(InvoicePayment obj);

    public List<InvoicePayment> listOfPayments(Invoice invoice);

    public int delete(InvoicePayment invoicePayment);

    public List<CheckDelete> testBeforeDelete(InvoicePayment invoicePayment);
}
