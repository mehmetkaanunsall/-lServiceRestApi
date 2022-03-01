/**
 * Bu sınıf, ApplicationDao sınıfına arayüz oluşturur.
 *
 *
 * @author Salem walaa Abdulhadie
 *
 * @date   20.07.2016 17:01:16
 */
package com.mepsan.marwiz.general.appllication.dao;

import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.ScheduledJobTrigger;
import com.mepsan.marwiz.general.model.wot.ApplicationList;
import java.util.Map;

public interface IApplicationDao {

    public ApplicationList appListXml();

    public String countryListXml();

    public String appListOfPages();

    public String modules();

    public Map<Integer, BranchSetting> bringBranchSettings();

    public int controlAutomationSettingBranch(int automationId);

    public int controlStarbucksSettingBranch();

    public Map scheduledJob();

    public ScheduledJobTrigger findScheduledJob(int id);

    public int controlBranchIntegration();

}
