/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 25.06.2018 08:11:40
 */
package com.mepsan.marwiz.finance.invoice.dao;

import com.mepsan.marwiz.general.model.finance.Invoice;
import com.mepsan.marwiz.general.model.finance.InvoicePayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.util.List;

public interface IInvoicePaymentDao {

    public int create(InvoicePayment obj, String json);

    public int update(InvoicePayment obj, String json);

    public List<InvoicePayment> listOfPayments(Invoice invoice);

    public int delete(InvoicePayment invoicePayment);

    public List<CheckDelete> testBeforeDelete(InvoicePayment invoicePayment);
}
