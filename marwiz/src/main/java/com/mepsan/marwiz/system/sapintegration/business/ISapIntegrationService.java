/**
 * 
 *
 *
 * @author Ali Kurt
 *
 * @date 29.04.2019 08:55:23 
 */

package com.mepsan.marwiz.system.sapintegration.business;

import java.util.Date;
import java.util.List;


public interface ISapIntegrationService {
    
    public List<SapIntegration> listOfCollections(Date begin,Date end,int isSend);
    
    public List<SapIntegration> listOfSafeToBank(Date begin,Date end,int isSend);
    
    public boolean sendCollections(List<SapIntegration> litOfSocarIntegration);
    
    public boolean sendSafeToBank(List<SapIntegration> litOfSocarIntegration);
    
}

