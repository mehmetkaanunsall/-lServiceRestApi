/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.business;

import com.google.gson.JsonObject;
import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.finance.credit.dao.ICreditPaymentDao;
import com.mepsan.marwiz.general.model.finance.CreditPayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.system.Currency;
import java.text.SimpleDateFormat;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author Gozde Gursel
 */
public class CreditPaymentService implements ICreditPaymentService {

    @Autowired
    private ICreditPaymentDao creditPaymentDao;

    public void setCreditPaymentDao(ICreditPaymentDao creditPaymentDao) {
        this.creditPaymentDao = creditPaymentDao;
    }

    @Override
    public List<CreditPayment> listCreditPayment(CreditReport credit) {
        return creditPaymentDao.listCreditPayment(credit);
    }

    @Override
    public int create(CreditPayment creditPayment, Currency currencys) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        JsonObject jsonObject = null;
        if (creditPayment.getType().getId() == 66 || creditPayment.getType().getId() == 69) {
            jsonObject = new JsonObject();
            jsonObject.addProperty("portfolionumber", creditPayment.getChequeBill().getPortfolioNumber());
            jsonObject.addProperty("expirydate", sdf.format(creditPayment.getChequeBill().getExpiryDate()));
            jsonObject.addProperty("bankbranch_id", creditPayment.getChequeBill().getBankBranch().getId() == 0 ? null : creditPayment.getChequeBill().getBankBranch().getId());
            jsonObject.addProperty("documentnumber_id", creditPayment.getChequeBill().getDocumentNumber().getId() == 0 ? null : creditPayment.getChequeBill().getDocumentNumber().getId());
            jsonObject.addProperty("documentserial", creditPayment.getChequeBill().getDocumentSerial());
            jsonObject.addProperty("documentnumber", creditPayment.getChequeBill().getDocumentNumber().getId() == 0 ? null : creditPayment.getChequeBill().getDocumentNo());
            jsonObject.addProperty("accountnumber", creditPayment.getChequeBill().getAccountNumber());
            jsonObject.addProperty("ibannumber", creditPayment.getChequeBill().getIbanNumber());
            jsonObject.addProperty("status_id", creditPayment.getChequeBill().getStatus().getId());
            jsonObject.addProperty("paymentcity_id", creditPayment.getChequeBill().getPaymentCity().getId());
            jsonObject.addProperty("accountguarantor", creditPayment.getChequeBill().getAccountGuarantor());

            switch (creditPayment.getType().getId()) {
                case 66://çek ise detay ekle
                    jsonObject.addProperty("is_cheque", Boolean.TRUE);
                    break;
                case 69:
                    jsonObject.addProperty("is_cheque", Boolean.FALSE);
                    jsonObject.addProperty("bill_collocationdate", sdf.format(creditPayment.getChequeBill().getBillCollocationDate()));
                    jsonObject.addProperty("accountguarantor", creditPayment.getChequeBill().getAccountGuarantor());
                    break;
            }
        }
        //  System.out.println("-----json" + jsonObject == null ? "{}" : jsonObject.toString());
        return creditPaymentDao.create(creditPayment, currencys, jsonObject == null ? "{}" : jsonObject.toString());

    }

    @Override
    public List<CheckDelete> testBeforeDelete(CreditPayment creditPayment) {
        return creditPaymentDao.testBeforeDelete(creditPayment);
    }

    @Override
    public int delete(CreditPayment creditPayment) {
        return creditPaymentDao.delete(creditPayment);

    }

    @Override
    public int update(CreditPayment creditPayment) {
        return creditPaymentDao.update(creditPayment);
    }

}
