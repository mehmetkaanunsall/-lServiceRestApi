/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.05.2020 09:41:28
 */
package com.mepsan.marwiz.service.backup.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.service.backup.dao.IGetBackUpParameteDao;
import com.mepsan.marwiz.service.client.WebServiceClient;
import org.springframework.beans.factory.annotation.Autowired;

public class GetBackUpParameterService implements IGetBackUpParameterService {

    @Autowired
    private IGetBackUpParameteDao getBackUpParameteDao;

    public void setGetBackUpParameteDao(IGetBackUpParameteDao getBackUpParameteDao) {
        this.getBackUpParameteDao = getBackUpParameteDao;
    }

    @Override
    public int listBackUpParameters(BranchSetting branchSetting) {

        String res = null;
        try {
            
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                      = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                      + "    <SOAP-ENV:Header/>\n"
                      + "    <S:Body>\n"
                      + "        <ns2:GetStationLastIds xmlns:ns2=\"http://ws/\">\n"
                      + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                      + "        </ns2:GetStationLastIds>\n"
                      + "    </S:Body>\n"
                      + "</S:Envelope>";

            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            
        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
        }
        return getBackUpParameteDao.callTableSequence(res);
    }

}
