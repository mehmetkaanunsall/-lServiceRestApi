/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   06.11.2019 03:23:02
 */
package com.mepsan.marwiz.general.report.dailysalesreport.dao;


public interface IDailySalesReportDao  {

    public DailySalesReport findAll(DailySalesReport dailySalesReport, String branchList, String sortBy);

}
