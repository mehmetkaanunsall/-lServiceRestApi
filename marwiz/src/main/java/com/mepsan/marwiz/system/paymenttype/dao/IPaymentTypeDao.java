/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.paymenttype.dao;

import com.mepsan.marwiz.general.model.general.PaymentType;
import com.mepsan.marwiz.general.pattern.ICrud;
import java.util.List;

/**
 *
 * @author m.duzoylum
 */
public interface IPaymentTypeDao extends ICrud<PaymentType> {

    public List<PaymentType> listofPayment(int branchId);

    public int delete(PaymentType obj);
}
