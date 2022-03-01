/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.duepaymentsreport.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.pattern.IReportService;
import com.mepsan.marwiz.general.report.duepaymentsreport.dao.DuePaymentsReport;
import java.util.List;
import java.util.Map;

/**
 *
 * @author ebubekir.buker
 */
public interface IDuePaymentsReportService  extends IReportService<DuePaymentsReport>{//servis interface sindeki alanlar ?
    
    public List<DuePaymentsReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where, String whereBranch, DuePaymentsReport duePaymentsReport);
    
    public List<DuePaymentsReport>totals(String where);//her paginator için alt toplam yapacak
    
    public void exportPdf(String where, DuePaymentsReport duePaymentsReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList , String totalRemainingMoney);

    public void exportExcel(String where, DuePaymentsReport duePaymentsReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList , String totalRemainingMoney);

    public String exportPrinter(String where, DuePaymentsReport duePaymentsReport, List<Boolean> toogleList, String branchList, List<BranchSetting> selectedBranchList ,String totalRemainingMoney);

    
}
