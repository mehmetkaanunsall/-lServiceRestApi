/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.paymenttype.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.PaymentType;
import com.mepsan.marwiz.system.paymenttype.dao.IPaymentTypeDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author m.duzoylum
 */
public class PaymentTypeService implements IPaymentTypeService {

    @Autowired
    private IPaymentTypeDao paymentTypeDao;

    @Autowired
    private SessionBean sessionBean;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public List<PaymentType> listofPayment(int branchId) {
        return paymentTypeDao.listofPayment(branchId);
    }

    @Override
    public int delete(PaymentType obj) {
        return paymentTypeDao.delete(obj);
    }

    @Override
    public int create(PaymentType obj) {
        return paymentTypeDao.create(obj);
    }

    @Override
    public int update(PaymentType obj) {
        return paymentTypeDao.update(obj);
    }

}
