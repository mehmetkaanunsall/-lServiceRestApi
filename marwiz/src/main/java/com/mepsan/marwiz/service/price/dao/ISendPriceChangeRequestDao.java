/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 11:44:27
 */
package com.mepsan.marwiz.service.price.dao;

import com.mepsan.marwiz.general.model.inventory.StockPriceRequest;
import com.mepsan.marwiz.general.model.log.SendPriceChangeRequest;
import com.mepsan.marwiz.general.model.log.SendPriceChangeRequestCheck;
import java.util.List;

public interface ISendPriceChangeRequestDao {

    public List<SendPriceChangeRequest> findNotSendedAll();
    public SendPriceChangeRequest findByIdPriceChangeRequestId(int stockRequestId);
    public int updatePriceChangeRequestResult(SendPriceChangeRequest sendPriceChangeRequest);
    public List<SendPriceChangeRequestCheck> findNotAprovedAll();
    public int updatePriceChangeRequest(StockPriceRequest stockRequest);
    public int updatePriceChangeRequestCheckResponse(int requestid,String res);
}
