/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 25.04.2018 11:43:14
 */
package com.mepsan.marwiz.service.stock.business;

import com.mepsan.marwiz.general.model.inventory.StockRequest;
import com.mepsan.marwiz.general.model.log.SendStockRequestCheck;
import com.mepsan.marwiz.general.model.log.SendStockRequest;
import java.util.List;

public interface ISendStockRequestService {

    public void sendStockRequest(int stockRequestId);

    public void sendStockRequest(SendStockRequest sendStockRequest);

    public void sendStockRequestAsync(SendStockRequest sendStockRequest);

    public void sendNotSendedStockRequestAsync();

    public void checkStockRequestAsync();
    
    public void checkStockRequest(SendStockRequestCheck requestCheck);
    
    public SendStockRequest findByIdStockRequestId(int stockRequestId);
    
    public List<SendStockRequestCheck> findStockRequest(StockRequest stockRequest);
    
    
}
