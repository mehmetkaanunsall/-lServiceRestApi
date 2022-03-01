/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 12.09.2018 11:34:10 
 */
package com.mepsan.marwiz.service.stock.dao;

import com.mepsan.marwiz.general.model.log.SendStockInfo;
import java.util.List;

public interface ISendStockInfoDao {

    public List<SendStockInfo> getStockInfoData();

    public int insertResult(SendStockInfo sendStockInfo);
}
