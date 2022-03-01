package com.mepsan.marwiz.system.einvoiceintegration.business;

import com.mepsan.marwiz.general.model.general.Account;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.SendEInvoice;
import com.mepsan.marwiz.system.einvoiceintegration.dao.EInvoice;
import java.util.Date;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IEInvoiceIntegrationService {

    public String createWhere(Date beginDate, Date endDate, int isSend, int processType, List<Account> accountList, String invoiceNo);

    public boolean sendEInvoice(List<EInvoice> listEInvoice, BranchSetting obj);

    public boolean sendEArchive(List<EInvoice> listEInvoice, BranchSetting obj);

    public void sendEInvoicewebService(String data, List<EInvoice> listOfInvoices);

    public List<EInvoice> listOfEInvoices(String where, int operationType);

    public List<SendEInvoice> listSendEInvocie();

    public void invoiceStatusInquiry(List<SendEInvoice> listSendEInvoice);

    public void sendUEInvoice(List<EInvoice> listEInvoice, BranchSetting obj);

    public void sendUEArchive(List<EInvoice> listEInvoice, BranchSetting obj);

    public void sendEInvoiceUWebservice(String data, List<EInvoice> listOfInvoices);

    public void uInvoiceStatusInquiry(List<SendEInvoice> listSendEInvoice);

    public BranchSetting bringBranchAdress();

    public int updateArchive(String ids, int updateType);
    
     public int createLogForArchive(List<EInvoice> listOfInsert);

}
