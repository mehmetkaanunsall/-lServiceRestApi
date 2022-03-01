/**
 * Bu interface ApplicationService .
 *
 *
 * @author Salem walaa Abdulhadie
 *
 * @date   20.07.2016 17:01:16
 */
package com.mepsan.marwiz.general.appllication.business;

import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.wot.ApplicationList;
import com.mepsan.marwiz.general.model.admin.Parameter;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.general.ScheduledJobTrigger;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Language;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface IApplicationService {

    public ApplicationList appListXml();

    public List<Parameter> parameterList(String xml);

    public List<Status> customizeStatusXml(String xml);

    public List<Country> countryListXml();

    public List<Currency> customizeCurrencyXml(String xml);

    public List<Language> customizeLangXml(String xml);

    public List<Type> customizeTypeXml(String xml);

    public List<Module> customizeModules();

    public HashMap<Integer, Boolean> customizeBranchShiftPaymentXml(String xml);

    public Map<Integer, BranchSetting> bringBranchSettings();

    public int controlAutomationSettingBranch(int automationId);
    
    public int controlStarbucksSettingBranch();
    
    public Map<Integer, ScheduledJobTrigger> scheduledJob();
    
    public ScheduledJobTrigger findScheduledJob(int id);
    
    public int controlBranchIntegration();
}
