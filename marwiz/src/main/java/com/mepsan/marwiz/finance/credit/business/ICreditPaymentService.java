/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.business;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.general.model.finance.CreditPayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.model.system.Currency;
import java.util.List;

/**
 *
 * @author Gozde Gursel
 */
public interface ICreditPaymentService {

    public List<CreditPayment> listCreditPayment(CreditReport credit);

    public int create(CreditPayment creditPayment, Currency currencys);

    public int update(CreditPayment creditPayment);

    public int delete(CreditPayment creditPayment);

    public List<CheckDelete> testBeforeDelete(CreditPayment creditPayment);
}
