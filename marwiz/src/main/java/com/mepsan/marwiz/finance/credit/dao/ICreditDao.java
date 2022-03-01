/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.finance.credit.dao;

import com.mepsan.marwiz.general.model.general.CheckDelete;
import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.Date;
import java.util.List;

/**
 *
 * @author Gozde Gursel
 */
public interface ICreditDao extends ILazyGrid<CreditReport>{    
    
    public List<CheckDelete> testBeforeDelete(CreditReport credit);

    public int delete(CreditReport chequeBill);
    
    public List<CreditReport> findShiftCredit(Date beginDate,Date endDate);
    
}
