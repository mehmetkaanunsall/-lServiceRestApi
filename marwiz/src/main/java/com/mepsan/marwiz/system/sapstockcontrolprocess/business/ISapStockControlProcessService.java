/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapstockcontrolprocess.business;

import com.mepsan.marwiz.system.sapstockcontrolprocess.dao.SapStockControlProcess;
import java.util.Date;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface ISapStockControlProcessService {

    public SapStockControlProcess getSapStockInfos(Date date);

    public List<SapStockControlProcess> compareStockInfos(SapStockControlProcess sapStock, Date date, int differenceReasonType);

    public void exportExcel(List<Boolean> toogleList, List<SapStockControlProcess> listOfDifferentStocks, Date date, int differenceReasonType);

    public void exportPdf(List<Boolean> toogleList, List<SapStockControlProcess> listOfDifferentStocks, Date date, int differenceReasonType);

}
