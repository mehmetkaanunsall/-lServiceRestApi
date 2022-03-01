/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.general.report.deficitcreditreport.business;

import com.mepsan.marwiz.finance.credit.dao.CreditReport;
import com.mepsan.marwiz.general.model.general.Branch;
import java.util.List;
import java.util.Map;

/**
 *
 * @author samet.dag
 */
public interface IDeficitCreditService {

    public List<CreditReport> findAll(int first, int pageSize, String sortField, String sortOrder, Map<String, Object> filters, String where);

    public String createWhere(CreditReport obj);

    public void exportPdf(String where, List<Boolean> toogleList, List<Branch> selectedBranchList, CreditReport deficitCredit, String totalMoney, String paidMoney, String remainingMoney);

    public void exportExcel(String where, List<Boolean> toogleList, List<Branch> selectedBranchList, CreditReport deficitCredit, String totalMoney, String paidMoney, String remainingMoney);

    public String exportPrinter(String where, List<Boolean> toogleList, List<Branch> selectedBranchList, CreditReport deficitCredit, String totalMoney, String paidMoney, String remainingMoney);

    public List<CreditReport> totals(String where);
}
