/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   13.07.2020 09:43:05
 */
package com.mepsan.marwiz.service.branchinfo.business;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.service.branchinfo.dao.IGetBranchInfoDao;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.springframework.beans.factory.annotation.Autowired;

public class GetBranchInfoService implements IGetBranchInfoService {

    @Autowired
    private IGetBranchInfoDao getBranchInfoDao;

    public void setGetBranchInfoDao(IGetBranchInfoDao getBranchInfoDao) {
        this.getBranchInfoDao = getBranchInfoDao;
    }

    @Override
    public int listBranchInfo(BranchSetting branchSetting) {

        String res = null;
        try {

            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                      = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                      + "    <SOAP-ENV:Header/>\n"
                      + "    <S:Body>\n"
                      + "        <ns2:GetLicenceCode xmlns:ns2=\"http://ws/\">\n"
                      + "            <station><![CDATA[" + branchSetting.getBranch().getLicenceCode() + "]]></station>\n"
                      + "        </ns2:GetLicenceCode>\n"
                      + "    </S:Body>\n"
                      + "</S:Envelope>";

            res = webServiceClient.request(branchSetting.getwSendPoint() + "/WsIncome?xsd=1", branchSetting.getWebServiceUserName(), branchSetting.getWebServicePassword(), data);
            
        } catch (Exception ex) {
            res = res + "---Error:" + ex.getMessage();
        }
        return getBranchInfoDao.callBranchInfo(branchSetting.getBranch().getId(), res);
    }

    public void callBranchInfoForAllBranches() {
        List<BranchSetting> branchSettings = getBranchInfoDao.findBranchSettingsForBranchInfo();
        executeCreateList(branchSettings);
    }

    @Override
    public void executeCreateList(List<BranchSetting> branchSettings) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (BranchSetting branchSetting : branchSettings) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    listBranchInfo(branchSetting);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

}
