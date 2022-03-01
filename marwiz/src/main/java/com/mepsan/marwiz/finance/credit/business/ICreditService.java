/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.business;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.pattern.ICrudService;
import com.mepsan.marwiz.general.pattern.ILazyGridService;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gozde Gursel
 */
public interface ICreditService extends ICrudService<CreditReport>, ILazyGridService<CreditReport> {

    public CreditReport findCreditReport(CreditReport obj);

    public List<CheckDelete> testBeforeDelete(CreditReport credit);

    public int delete(CreditReport chequeBill);

    public List<CreditReport> findShiftCredit(Date beginDate, Date endDate);

    public String createWhere(Branch branch);

}
