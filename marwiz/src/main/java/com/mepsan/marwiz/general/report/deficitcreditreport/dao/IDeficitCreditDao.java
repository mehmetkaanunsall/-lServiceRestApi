/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.deficitcreditreport.dao;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import java.util.List;
import javax.sql.DataSource;

/**
 *
 * @author samet.dag
 */
public interface IDeficitCreditDao {

    public String exportData(String where);

    public DataSource getDatasource();
    
    public List<CreditReport> totals(String where);
    
}
