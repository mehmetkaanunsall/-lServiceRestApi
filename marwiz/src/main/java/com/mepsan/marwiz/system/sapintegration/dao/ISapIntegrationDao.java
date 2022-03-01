/**
 * 
 *
 *
 * @author Ali Kurt
 *
 * @date 29.04.2019 10:46:55 
 */

package com.mepsan.marwiz.system.sapintegration.dao;

import com.mepsan.marwiz.general.model.log.SendSap;
import com.mepsan.marwiz.system.sapintegration.business.SapIntegration;
import java.util.Date;
import java.util.List;


public interface ISapIntegrationDao {
    
    public List<SapIntegration> listOfCollections(Date begin,Date end,int isSend);
    
    public List<SapIntegration> listOfSafeToBank(Date begin,Date end,int isSend);
    
    public int insertOrUpdateLog(SendSap sapResult);
    
}

