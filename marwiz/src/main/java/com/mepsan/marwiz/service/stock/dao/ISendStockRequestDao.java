/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 11:44:27
 */
package com.mepsan.marwiz.service.stock.dao;

import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.log.SendStockRequestCheck;
import com.mepsan.marwiz.general.model.log.SendStockRequest;
import java.util.List;

public interface ISendStockRequestDao {

    public List<SendStockRequest> findNotSendedAll();
    public SendStockRequest findByIdStockRequestId(int stockRequestId);
    public int updateStockRequestResult(SendStockRequest sendStockRequest);
    public List<SendStockRequestCheck> findNotAprovedAll();
    public int updateStockRequest(StockRequest stockRequest);
    public int updateStockRequestCheckResponse(int requestid,String res);
    public List<SendStockRequestCheck> findStockRequest(StockRequest stockRequest);
}
