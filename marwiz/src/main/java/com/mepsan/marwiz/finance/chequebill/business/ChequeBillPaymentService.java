/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.business;

import com.mepsan.marwiz.finance.chequebill.dao.IChequeBillPaymentDao;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.ChequeBillPayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author esra.cabuk
 */
public class ChequeBillPaymentService implements IChequeBillPaymentService{

    @Autowired
    private IChequeBillPaymentDao chequeBillPaymentDao;
    
    public void setChequeBillPaymentDao(IChequeBillPaymentDao chequeBillPaymentDao) {
        this.chequeBillPaymentDao = chequeBillPaymentDao;
    }

    
    @Override
    public List<ChequeBillPayment> listChequeBillPayment(ChequeBill chequeBill) {
        return chequeBillPaymentDao.listChequeBillPayment(chequeBill);
    }

   
    @Override
    public int create(ChequeBillPayment chequeBillPayment) {
        return chequeBillPaymentDao.create(chequeBillPayment);
    }

    @Override
    public int delete(ChequeBillPayment chequeBillPayment) {
        return chequeBillPaymentDao.delete(chequeBillPayment);
    }

    @Override
    public int update(ChequeBillPayment chequeBillPayment) {
        return chequeBillPaymentDao.update(chequeBillPayment);
    }

    @Override
    public List<CheckDelete> testBeforeDelete(ChequeBillPayment chequeBillPayment) {
       return chequeBillPaymentDao.testBeforeDelete(chequeBillPayment);
    }
    
}
