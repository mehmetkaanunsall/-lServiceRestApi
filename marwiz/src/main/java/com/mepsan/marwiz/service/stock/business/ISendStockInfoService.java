/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 12.09.2018 12:12:42
 */
package com.mepsan.marwiz.service.stock.business;

import com.mepsan.marwiz.general.model.log.SendStockInfo;
import java.util.List;

public interface ISendStockInfoService {

    public void sendStockInfo(SendStockInfo sendStockInfo);

    public void sendStockInfoAsync();

    public void executeSendStockInfo(List<SendStockInfo> sendStockInfos);
     
}
