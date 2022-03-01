/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mepsan.marwiz.system.sapstockcontrolprocess.dao;

import java.util.Date;
import java.util.List;

/**
 *
 * @author elif.mart
 */
public interface ISapStockControlProcessDao {

    public List<SapStockControlProcess> compareStockInfos(SapStockControlProcess sapStock, Date date, int differenceReasonType);

    public int insertOrUpdateLog(SapStockControlProcess obj);

}
