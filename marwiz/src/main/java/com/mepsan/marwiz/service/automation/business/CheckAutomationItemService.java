/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   15.05.2019 10:48:29
 */
package com.mepsan.marwiz.service.automation.business;

import com.mepsan.marwiz.general.httpclient.business.AESEncryptor;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.log.CheckAutomationItem;
import com.mepsan.marwiz.service.client.WebServiceClient;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.springframework.beans.factory.annotation.Autowired;
import com.mepsan.marwiz.service.automation.dao.ICheckAutomationItemDao;
import java.text.ParseException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

public class CheckAutomationItemService implements ICheckAutomationItemService {

    @Autowired
    private ICheckAutomationItemDao checkAutomationItemDao;

    public void setCheckAutomationItemDao(ICheckAutomationItemDao checkAutomationItemDao) {
        this.checkAutomationItemDao = checkAutomationItemDao;
    }

    @Override
    public void listAutomationShift(BranchSetting branchSetting) {
        int type = 1;
        Date maxProcessDateByType = checkAutomationItemDao.getMaxProcessDateByType(type, branchSetting);

        CheckAutomationItem checkAutomationItem = new CheckAutomationItem();
        checkAutomationItem.setType(type);
        checkAutomationItem.setProcessDate(new Date());
        checkAutomationItem.setIsSuccess(false);
        String res = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss.SSS");
            String processDateString = null;
            if (maxProcessDateByType != null) {
                processDateString = simpleDateFormat.format(maxProcessDateByType);
            } else {
                processDateString = simpleDateFormat.format(new Date(0));
            }
            AESEncryptor aes = new AESEncryptor();
            WebServiceClient webServiceClient = new WebServiceClient();
            String data
                      = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><S:Envelope xmlns:S=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\">\n"
                      + "    <SOAP-ENV:Header/>\n"
                      + "    <S:Body>\n"
                      + "        <ns2:GetAutomationData xmlns:ns2=\"http://ws/\">\n"
                      + "            <station>" + aes.encrypt(branchSetting.getBranch().getLicenceCode()) + "</station>\n"
                      + "            <type>" + aes.encrypt("1") + "</type>\n"
                      + "            <processdate>" + aes.encrypt(processDateString) + "</processdate>\n"
                      + "        </ns2:GetAutomationData>\n"
                      + "    </S:Body>\n"
                      + "</S:Envelope>";
            res = webServiceClient.requestAes(branchSetting.getAutomationUrl() + "/WsIncome?xsd=1", branchSetting.getAutomationUserName(), branchSetting.getAutomationPassword(), data);

            SimpleDateFormat myFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

            JSONObject resJson = new JSONObject(res);
            boolean resBoolean = resJson.getBoolean("Result");
            checkAutomationItem.setIsSuccess(resBoolean);
            checkAutomationItem.setResponse(resJson.getJSONArray("Response").toString());

            if (resBoolean) {
                Date processDate = myFormat.parse(resJson.getString("ProcessDate"));
                checkAutomationItem.setProcessDate(processDate);
            }
           
        } catch (Exception ex) {
            res = res + "---Error:" + ex.toString();
            checkAutomationItem.setResponse(res);
        }
        checkAutomationItemDao.insertAutomationItem(checkAutomationItem, branchSetting);
    }

    @Override
    public void listAutomationShiftAsync() {
        List<BranchSetting> branchSettings = checkAutomationItemDao.findAutomationIntegratedBranchSettings();
        executeListAutomationShift(branchSettings);
    }

    @Override
    public void executeListAutomationShift(List<BranchSetting> branchSettings) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        for (BranchSetting branchSetting : branchSettings) {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    listAutomationShift(branchSetting);
                }
            };
            fixedThreadPool.submit(runnable);
        }
        fixedThreadPool.shutdown();
    }

}
