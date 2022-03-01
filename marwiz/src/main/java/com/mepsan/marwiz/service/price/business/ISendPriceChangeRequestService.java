/**
 *
 * @author Mehmet ERGÜLCÜ
 * @date 12.02.2019 08:05:13
 */
package com.mepsan.marwiz.service.price.business;

import com.mepsan.marwiz.general.model.log.SendPriceChangeRequest;
import com.mepsan.marwiz.general.model.log.SendPriceChangeRequestCheck;

public interface ISendPriceChangeRequestService {

    public void sendPriceChangeRequest(int requestId);

    public void sendPriceChangeRequest(SendPriceChangeRequest sendPriceChangeRequest);

    public void sendPriceChangeRequestAsync(SendPriceChangeRequest sendPriceChangeRequest);

    public void sendNotSendedPriceChangeRequestAsync();

    public void checkPriceChangeRequestAsync();
    
    public void checkPriceChangeRequest(SendPriceChangeRequestCheck sendPriceRequestCheck);
    
    
}
