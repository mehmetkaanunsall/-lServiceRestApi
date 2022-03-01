/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2018 02:42:36
 */
package com.mepsan.marwiz.finance.customeragreements.business;

import com.mepsan.marwiz.finance.customeragreements.dao.CustomerAgreements;
import com.mepsan.marwiz.finance.customeragreements.dao.ICustomerAgreementsDao;
import com.mepsan.marwiz.general.model.general.Account;
import java.text.SimpleDateFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class CustomerAgreementsService implements ICustomerAgreementsService {

    @Autowired
    private ICustomerAgreementsDao customerAgreementsDao;

    public void setCustomerAgreementsDao(ICustomerAgreementsDao customerAgreementsDao) {
        this.customerAgreementsDao = customerAgreementsDao;
    }

    @Override
    public String createWhere(CustomerAgreements obj) {
        String where = "";
        SimpleDateFormat sd = new SimpleDateFormat("MM.dd.yyyy HH:mm:ss");

        where += " AND crdt.processdate BETWEEN '" + sd.format(obj.getBeginDate()) + "' AND '" + sd.format(obj.getEndDate()) + "' ";

        where += " AND crdt.branch_id =" + obj.getBranchSetting().getBranch().getId() + " ";

        String accountList = "";
        for (Account account : obj.getListOfAccount()) {
            accountList = accountList + "," + String.valueOf(account.getId());
            if (account.getId() == 0) {
                accountList = "";
                break;
            }
        }
        if (!accountList.equals("")) {
            accountList = accountList.substring(1, accountList.length());
            where = where + " AND crdt.account_id IN(" + accountList + ") ";
        }

        switch (obj.getInvoiceType()) {//yeni eklendi
            case 0:
                where = where + " AND crdt.is_invoice = FALSE";
                break;
            case 1:
                where = where + " AND crdt.is_invoice = TRUE";
                break;
            case 2:
        }

        if (obj.isChcCredit() && obj.getCreditType() == 2) {
            if (obj.getPlate() != null) {
                where = where + " AND sh.plate ilike '%" + obj.getPlate() + "%' ";

            }
        }

        return where;
    }

    @Override
    public List<CustomerAgreements> findAll(String where, boolean checkCreditControl, int creditType) {
        return customerAgreementsDao.findAll(where, checkCreditControl, creditType);
    }

}
