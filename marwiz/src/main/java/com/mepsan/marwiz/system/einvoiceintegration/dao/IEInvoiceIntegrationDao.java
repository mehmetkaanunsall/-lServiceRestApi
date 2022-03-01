package com.mepsan.marwiz.system.einvoiceintegration.dao;

import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.SendEInvoice;
import com.mepsan.marwiz.system.branch.business.BranchService;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface IEInvoiceIntegrationDao {

    public List<EInvoice> listOfEInvoices(String where, int operationType);

    public void insertOrUpdateLog(List<SendEInvoice> integrations, Boolean isStatus);

    public List<SendEInvoice> listSendEInvocie();

    public BranchSetting bringBranchAdress();

    public int updateArchive(String ids, int updateType);

    public int createLogForArchive(List<EInvoice> listOfInsert);

}
