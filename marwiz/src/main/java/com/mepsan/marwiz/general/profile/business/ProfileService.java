/**
 *
 *
 *
 * @author Cihat Kucukbagriacik
 *
 * Created on 21.09.2016 10:24:20
 */
package com.mepsan.marwiz.general.profile.business;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.httpclient.business.AESEncryptor;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.profile.dao.IProfileDao;
import com.mepsan.marwiz.service.client.WebServiceClient;
import com.mepsan.marwiz.system.branch.dao.IBranchSettingDao;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

public class ProfileService implements IProfileService {

    @Autowired
    private IProfileDao profileDao;

    @Autowired
    private SessionBean sessionBean;

    @Autowired
    private IBranchSettingDao branchSettingDao;

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public void setProfileDao(IProfileDao profileDao) {
        this.profileDao = profileDao;
    }

    public void setBranchSettingDao(IBranchSettingDao branchSettingDao) {
        this.branchSettingDao = branchSettingDao;
    }

    @Override
    public int themeChange(String themeName) {
        //  sessionBean.getUser().getUserConfig().setTheme(themeName);
        return profileDao.themeChange(themeName);
    }

    @Override
    public String findLicanse() {
        String res = null;
        String resultData = res;

        BranchSetting branchSetting = null;
        branchSetting = branchSettingDao.findLicanseCode();

        JsonObject resultJson = new JsonObject();

        resultJson.addProperty("ipaddress", branchSetting.getLocalServerIpAddress());
        resultJson.addProperty("licancecode", branchSetting.getBranch().getLicenceCode());
        resultJson.addProperty("branchname", branchSetting.getBranch().getName());
        resultJson.addProperty("licenceurl", "http://stawiz.mepsan.com.tr:8888/StawizWallet/Wallet");
        resultJson.addProperty("licencetype", "basic");

        AESEncryptor aes = new AESEncryptor();
        resultData = aes.encrypt(resultJson.toString());

        return resultData;
    }
}
