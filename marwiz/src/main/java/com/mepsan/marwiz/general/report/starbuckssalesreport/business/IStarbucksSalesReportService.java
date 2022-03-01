/**
 *
 *
 *
 * @author Gozde Gursel
 *
 * Created on 3:16:10 PM
 */
package com.mepsan.marwiz.general.report.starbuckssalesreport.business;

import com.mepsan.marwiz.general.model.inventory.StarbucksStock;
import com.mepsan.marwiz.general.report.starbuckssalesreport.dao.StarbucksMachicneSales;
import java.util.Date;
import java.util.List;

public interface IStarbucksSalesReportService {

    public List<StarbucksMachicneSales> listOfSale(int first, int pageSize, Date begin, Date end, List<StarbucksStock> listOfStarbucksStock);

    public int count();

    public List<StarbucksMachicneSales> findSales(Date beginDate, Date endDate, List<StarbucksStock> listOfStarbucksStock);

    public void exportPdf(Date beginDate, Date endDate, StarbucksMachicneSales selectedObject, List<Boolean> toogleList, List<StarbucksMachicneSales> listOfSales, List<StarbucksStock> listOfStarbucksStock);

    public void exportExcel(Date beginDate, Date endDate, StarbucksMachicneSales selectedObject, List<Boolean> toogleList, List<StarbucksMachicneSales> listOfSales, List<StarbucksStock> listOfStarbucksStock);

    public String exportPrint(Date beginDate, Date endDate, StarbucksMachicneSales selectedObject, List<Boolean> toogleList, List<StarbucksMachicneSales> listOfSales, List<StarbucksStock> listOfStarbucksStock);

}
