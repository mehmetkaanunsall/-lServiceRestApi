package com.mepsan.marwiz.service.invoice.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.pattern.IReportService;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IInvoiceRequestService {

    public List<Account> sendTaxpayerİnquiryRequest(Account account);

    public Account requestTaxPayerİnquiryRequest(Account account);

    public Account requestAccountInfo(Account acc);

}
