/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.chequebill.business;

import com.mepsan.marwiz.finance.chequebill.presentation.ChequeBillBean;
import com.mepsan.marwiz.finance.chequebill.presentation.ChequeBillBean.ChequeBillParam;
import com.mepsan.marwiz.general.model.finance.ChequeBill;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.pattern.ICrudService;
import java.util.List;

/**
 *
 * @author esra.cabuk
 */
public interface IChequeBillService extends ICrudService<ChequeBill> {

    public List<ChequeBill> findAll(int chequeBillType, String where);

    public ChequeBill findChequeBill(ChequeBill obj);

    public List<CheckDelete> testBeforeDelete(ChequeBill chequeBill);

    public int delete(ChequeBill chequeBill);

    public String createWhere(ChequeBillParam searchObject, List<Branch> listOfBranch);
}
