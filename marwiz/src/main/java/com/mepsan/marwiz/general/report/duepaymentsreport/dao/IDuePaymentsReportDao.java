/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.duepaymentsreport.dao;

import com.mepsan.marwiz.general.pattern.ILazyGrid;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

/**
 *
 * @author ebubekir.buker
 */
public interface IDuePaymentsReportDao extends ILazyGrid<DuePaymentsReport> {
    
   public List <DuePaymentsReport>totals(String where);
   
   public List <DuePaymentsReport>findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch,DuePaymentsReport duePaymentsReport);
   
   public DataSource getDatasource();
   
   public String exportData(String where, String branchList, DuePaymentsReport duePaymentsReport);

   
}
