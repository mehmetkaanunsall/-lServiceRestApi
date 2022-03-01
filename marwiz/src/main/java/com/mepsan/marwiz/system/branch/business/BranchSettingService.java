/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   27.02.2018 02:00:25
 */
package com.mepsan.marwiz.system.branch.business;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Branch;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.PointOfSale;
import com.mepsan.marwiz.system.branch.dao.IBranchSettingDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

public class BranchSettingService implements IBranchSettingService {

    @Autowired
    public SessionBean sessionBean;

    @Autowired
    public IBranchSettingDao branchSettingDao;

    public void setBranchSettingDao(IBranchSettingDao branchSettingDao) {
        this.branchSettingDao = branchSettingDao;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    @Override
    public BranchSetting find(BranchSetting obj) {
        BranchSetting branchSetting = branchSettingDao.find(obj);
        return branchSetting;
    }

    @Override
    public int create(BranchSetting obj) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int update(BranchSetting obj) {

        String authPaymentTypeList = "";
        for (Integer a : obj.getlAuthPaymentType()) {
            authPaymentTypeList = authPaymentTypeList + "," + String.valueOf(a);
        }
        if (!authPaymentTypeList.equals("")) {
            authPaymentTypeList = authPaymentTypeList.substring(1, authPaymentTypeList.length());
        }
        obj.setAuthPaymentType(authPaymentTypeList);

        String authReportList = "";
        for (Integer a : obj.getlAuthReport()) {
            authReportList = authReportList + "," + String.valueOf(a);
        }
        if (!authReportList.equals("")) {
            authReportList = authReportList.substring(1, authReportList.length());
        }
        String printPaymentTypeList = "";
        for (Integer a : obj.getlPrintPaymentType()) {
            printPaymentTypeList = printPaymentTypeList + "," + String.valueOf(a);
        }
        if (!printPaymentTypeList.equals("")) {
            printPaymentTypeList = printPaymentTypeList.substring(1, printPaymentTypeList.length());
        }
        obj.setPrintPaymentType(printPaymentTypeList);
        return branchSettingDao.update(obj);
    }

    @Override
    public BranchSetting findCentralIntegration() {
        return branchSettingDao.findCentralIntegration();
    }

    @Override
    public BranchSetting findAutomationSetting(Branch obj) {
        return branchSettingDao.findAutomationSetting(obj);
    }

    @Override
    public BranchSetting findStarbucksMachicne() {
        return branchSettingDao.findStarbucksMachicne();
    }

    @Override
    public List<BranchSetting> findUserAuthorizeBranch() {
        return branchSettingDao.findUserAuthorizeBranch();
    }

    @Override
    public BranchSetting findBranchSetting(Branch branch) {
        return branchSettingDao.findBranchSetting(branch);
    }

    @Override
    public List<BranchSetting> findUserAuthorizeBranchForInvoiceAuth() {
        return branchSettingDao.findUserAuthorizeBranchForInvoiceAuth();
    }

    @Override
    public int updateParoInformation(BranchSetting obj, List<String> pointOfSaleIntegrationList) {
        return branchSettingDao.updateParoInformation(obj, pointOfSaleIntegrationList);
    }

   

}
