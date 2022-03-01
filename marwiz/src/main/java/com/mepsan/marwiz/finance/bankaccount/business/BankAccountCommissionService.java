/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   07.08.2020 08:46:47
 */
package com.mepsan.marwiz.finance.bankaccount.business;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.bankaccount.dao.IBankAccountCommissionDao;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.finance.BankAccountCommission;
import com.mepsan.marwiz.general.model.finance.FinancingDocument;
import java.text.SimpleDateFormat;
import org.springframework.beans.factory.annotation.Autowired;

public class BankAccountCommissionService implements IBankAccountCommissionService {

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IBankAccountCommissionDao bankAccountCommissionDao;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setBankAccountCommissionDao(IBankAccountCommissionDao bankAccountCommissionDao) {
        this.bankAccountCommissionDao = bankAccountCommissionDao;
    }

    @Override
    public int createCommission(BankAccountCommission bankAccountCommission) {
        return bankAccountCommissionDao.createCommission(bankAccountCommission, convertJsonBankAccountCommission(bankAccountCommission.getFinancingDocument()), convertJsonBankAccountCommission(bankAccountCommission.getCommissionFinancingDocument()));
    }

    @Override
    public int updateCommission(BankAccountCommission bankAccountCommission) {
        return bankAccountCommissionDao.updateCommission(bankAccountCommission, convertJsonBankAccountCommission(bankAccountCommission.getFinancingDocument()), convertJsonBankAccountCommission(bankAccountCommission.getCommissionFinancingDocument()));
    }

    public String convertJsonBankAccountCommission(FinancingDocument financingDocument) {
        JsonArray jsonArray = new JsonArray();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type_id", financingDocument.getFinancingType().getId());
        jsonObject.addProperty("financingdocument_id", financingDocument.getId());
        jsonObject.addProperty("incomeexpense_id", financingDocument.getIncomeExpense().getId() == 0 ? null : financingDocument.getIncomeExpense().getId());
        jsonObject.addProperty("is_incomeexpense", financingDocument.getIncomeExpense().getId() == 0 ? false : true);
        jsonObject.addProperty("is_chequebill", false);
        jsonObject.addProperty("documentnumber", financingDocument.getDocumentNumber());
        jsonObject.addProperty("price", financingDocument.getPrice());
        jsonObject.addProperty("currency_id", financingDocument.getCurrency().getId());
        jsonObject.addProperty("exchangerate", financingDocument.getExchangeRate());
        jsonObject.addProperty("documentdate", dateFormat.format(financingDocument.getDocumentDate()));
        jsonObject.addProperty("description", financingDocument.getDescription());
        jsonObject.addProperty("inmovement", financingDocument.getInMovementId());
        jsonObject.addProperty("outmovement", financingDocument.getOutMovementId());
        jsonObject.addProperty("branch_id", financingDocument.getBranch().getId());
        jsonObject.addProperty("transferbranch_id", financingDocument.getTransferBranch().getId());
        jsonObject.addProperty("userdata_id", sessionBean.getUser().getId());

        jsonArray.add(jsonObject);

        System.out.println("-----json---" + jsonArray.toString());
        return jsonArray.toString();
    }

    @Override
    public BankAccountCommission findBankAccountCommission(int bankAccountCommissionId) {
        return bankAccountCommissionDao.findBankAccountCommission(bankAccountCommissionId);
    }

    @Override
    public int deleteCommission(BankAccountCommission bankAccountCommission) {
        return bankAccountCommissionDao.deleteCommission(bankAccountCommission);
    }

}
