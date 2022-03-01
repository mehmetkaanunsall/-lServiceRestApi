/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.business;

import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.finance.ChequeBillPayment;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IChequeBillPaymentService {

    public List<ChequeBillPayment> listChequeBillPayment(ChequeBill chequeBill);

    public int create(ChequeBillPayment chequeBillPayment);

    public int update(ChequeBillPayment chequeBillPayment);

    public int delete(ChequeBillPayment chequeBillPayment);

    public List<CheckDelete> testBeforeDelete(ChequeBillPayment chequeBillPayment);
}
