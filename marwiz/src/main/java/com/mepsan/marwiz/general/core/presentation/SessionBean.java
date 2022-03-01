/**
 * Bu Sınıf ...Session Scope İçin
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   02.08.2018 14:56:19
 */
package com.mepsan.marwiz.general.core.presentation;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.common.StaticMethods;
import com.mepsan.marwiz.general.model.general.UserData;
import com.mepsan.marwiz.general.login.presentation.LoginBean;
import com.mepsan.marwiz.general.model.admin.Module;
import com.mepsan.marwiz.general.model.general.BranchSetting;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Language;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.Authentication;
import java.io.IOException;
import java.io.Serializable;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import org.primefaces.context.RequestContext;
import org.springframework.beans.factory.annotation.Autowired;

public class SessionBean implements Serializable {

    @Autowired
    private LoginBean loginBean;

    @Autowired
    private ApplicationBean applicationBean;

    private Locale locale;
    private UserData user;
    public ResourceBundle auth, loc;
    public Object parameter;
    private String none = "none";
    private List<Module> authorizedModules;
    private NumberFormat numberFormat;

    public NumberFormat getNumberFormat() {
        return numberFormat;
    }

    public void setNumberFormat(NumberFormat numberFormat) {
        this.numberFormat = numberFormat;
    }

    public BranchSetting getLastBranchSetting() {
        return applicationBean.getBranchSettingMap().get(user.getLastBranch().getId());
    }

    // private int workingType;
//    public int getWorkingType() {
//        return workingType;
//    }
//
//    public void setWorkingType(int workingType) {
//        this.workingType = workingType;
//    }
    public List<Module> getAuthorizedModules() {
        return authorizedModules;
    }

    public void setAuthorizedModules(List<Module> authorizedModules) {
        this.authorizedModules = authorizedModules;
    }

    public String getNone() {
        return none;
    }

    public void setNone(String none) {
        this.none = none;
    }

    public void setApplicationBean(ApplicationBean applicationBean) {
        this.applicationBean = applicationBean;
    }

    public ResourceBundle getAuth() {
        return auth;
    }

    public void setAuth(ResourceBundle auth) {
        this.auth = auth;
    }

    public ResourceBundle getLoc() {
        return loc;
    }

    public void setLoc(ResourceBundle loc) {
        this.loc = loc;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public Object getParameter() {
        return parameter;
    }

    public void setParameter(Object parameter) {
        this.parameter = parameter;
    }

    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    public UserData getUser() {
        return user;
    }

    public void setUser(UserData user) {

        this.user = user;
    }

    public void init() throws IOException {

        this.user = loginBean.getUser();

        user.setLastBranchSetting(applicationBean.getBranchSettingMap().get(user.getLastBranch().getId()));
        user.getLastBranchSetting().setBranch(user.getLastBranch());
        System.out.println(" *-* -*- *- " + this.user.getName());
        //  this.user.setCurrency(getCurrency(this.user.getCurrency().getId()));
        locale = new Locale(user.getLanguage().getCode().toLowerCase().trim());
        FacesContext.getCurrentInstance().getViewRoot().setLocale(locale);
        FacesContext context = FacesContext.getCurrentInstance();
        loc = context.getApplication().getResourceBundle(context, "loc");
        auth = context.getApplication().getResourceBundle(context, "auth");

        authorizedModules = new ArrayList<>();
        for (Module m : applicationBean.getListOfModules()) {
            if (user.getLastAuthorize().getListOfModules().contains(m.getId())) {
                authorizedModules.add(m);
            }
        }

        numberFormat = NumberFormat.getCurrencyInstance(locale);
        numberFormat.setMaximumFractionDigits(user.getLastBranch().getCurrencyrounding());
        numberFormat.setMinimumFractionDigits(user.getLastBranch().getCurrencyrounding());
        numberFormat.setRoundingMode(RoundingMode.HALF_EVEN);
//        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
//        decimalFormatSymbols.setMonetaryDecimalSeparator(user.getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
//        decimalFormatSymbols.setGroupingSeparator(user.getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
//        decimalFormatSymbols.setCurrencySymbol("");
//        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) numberFormat).getDecimalFormatSymbols();
        decimalFormatSymbols.setCurrencySymbol("");
        ((DecimalFormat) numberFormat).setDecimalFormatSymbols(decimalFormatSymbols);

        /*    if (user.getId() == 21) {
            Authentication a = new Authentication();
            a.setPageId(1);
            a.setUserId(user.getId());
            a.getList().add(1);

            Authentication a1 = new Authentication();
            a1.setPageId(11);
            a1.setUserId(user.getId());
           // a1.getList().add(2);

            user.getAuthentications().add(a);
            user.getAuthentications().add(a1);
        }
         */    
    }

    /**
     *
     * @param array
     * @param type 0 for buttons 1 for tabs 3 for modules
     * @return true or false (authenticated or unauthenticated)
     */
    public List<Integer> checkAuthority(int[] array, int type) {

        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < array.length; i++) {
            switch (type) {
                case 0:
                    if (user.getLastAuthorize().getListOfButtons().contains(array[i])) {
                        result.add(array[i]);
                    }
                    break;
                case 1:
                    if (user.getLastAuthorize().getListOfTabs().contains(array[i])) {
                        result.add(array[i]);
                    }
                    break;
                default:
            }
        }
        return result;
    }

    public String logoutPage() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "/pages/login?faces-redirect=true";

    }

    /**
     * gelen currency ıd bilgisine göre para bilgisini döndürür.
     *
     * @param currencyId gelen currency id si
     * @param type 0: kodu döndürür($,€,TL) 1: para birimini
     * döndürür(USD,EURO,TL)
     * @return
     */
    public String currencySignOrCode(int currencyId, int type) {
        for (Currency c : getCurrencies()) {
            if (c.getId() == currencyId) {
                if (type == 0) {
                    return c.getCode();
                } else {
                    return c.getSign();
                }
            }
        }
        return "";
    }

    /**
     * gelen currency ıd bilgisine göre para bilgisini döndürür.
     *
     * @param currencyId gelen currency id si
     * @return
     */
    public Currency getCurrency(int currencyId) {
        for (Currency c : getCurrencies()) {
            if (c.getId() == currencyId) {
                return c;
            }
        }
        return null;
    }

    /**
     *
     * @param item id
     * @return statu liste item id gore
     */
    public List<Status> getStatus(int item) {
        List<Status> result = new ArrayList<>();
        for (Status s : applicationBean.getStatuses()) {
            if (s.getItem().getId() == item) {
                result.add(s);
            }
        }
        return result;
    }

    /**
     *
     * @param item id
     * @return tip liste item id gore
     */
    public List<Type> getTypes(int item) {
        List<Type> result = new ArrayList<>();
        for (Type t : applicationBean.getTypes()) {
            if (t.getItem().getId() == item) {
                result.add(t);
            }
        }
        return result;
    }

    public List<Type> getCommunicationList() {
        List<Type> result = new ArrayList<>();
        for (Type type : applicationBean.getTypes()) {
            if (type.getItem().getId() == 34 || type.getItem().getId() == 35) {
                result.add(type);
            }
        }
        result.add(new Type(1, loc.getString("address")));
        return result;

    }

    /**
     *
     * @return language id for logged user
     */
    public int getLangId() {
        return user.getLanguage().getId();
    }

    /**
     *
     * @return butun ulkeler
     */
    public List<Country> getCountries() {
        return applicationBean.getCountries();
    }

    /**
     *
     * @return Currencies dile gore
     */
    public List<Currency> getCurrencies() {
        return applicationBean.getCurrencies();
    }

    /**
     *
     * @return butun diler dile gore
     */
    public List<Language> getLangList() {
        return applicationBean.getLanguages();
    }

    /**
     *
     * @param name
     * @param id
     * @return String property dosyadan adi ve id gore
     */
    public String findProperty(String name, int id) {
        return loc.getString(name + id);
    }

    public String getLanguageName(int langId) {
        for (Language language : applicationBean.getLanguages()) {
            if (language.getId() == langId) {
                return language.getNameMap().get(getLangId()).getName();
            }
        }
        return null;
    }

    public boolean checkPageAuthentication(int pageId) {
        List<Integer> systemPage = new ArrayList<>();
        systemPage.add(0);
        systemPage.add(-2);
        systemPage.add(-3);

        if (systemPage.contains(pageId)) {
            return true;
        } else {
            return getUser().getLastAuthorize().getListOfPages().contains(pageId);
        }
    }

    /**
     * cereate ve update işlemlerinde mesaj vermek için yazılmıstır
     *
     * @param result
     */
    public void createUpdateMessage(int result) {
        System.out.println("*---result---***" + result);
        FacesMessage message = new FacesMessage();
        if (result > 0) {//İŞLEM BASARILI
            message.setSummary(loc.getString("notification"));
            message.setDetail(loc.getString("succesfuloperation"));
            message.setSeverity(FacesMessage.SEVERITY_INFO);
        } else if (result == -23505) {//UNNİQE ALAN HATASI 
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(loc.getString("warning"));
            message.setDetail(loc.getString("thisrecordisavailableinthesystem"));
        } else if (result == -100001) {//TaRİH ARALIĞI AYNI HATASI
            message.setSeverity(FacesMessage.SEVERITY_WARN);
            message.setSummary(loc.getString("warning"));
            message.setDetail(loc.getString("recordisavailablethisdateinterval"));
        } else if (result <= 0) {//DİĞER HATA 
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            message.setSummary(loc.getString("error"));
            message.setDetail(loc.getString("unsuccesfuloperation"));
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        RequestContext context = RequestContext.getCurrentInstance();
        context.update("grwProcessMessage");
    }

    public String convertDate(Date date) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat format = new SimpleDateFormat(user.getLastBranch().getDateFormat());
        return format.format(date);
    }

    public String convertDateTime(Date date) {
        if (date == null) {
            date = new Date();
        }
        SimpleDateFormat format = new SimpleDateFormat(user.getLastBranch().getDateFormat() + " HH:mm:ss");
        return format.format(date);
    }

    public Boolean isPeriodClosed(Date date) {
        if (applicationBean.getBranchSettingMap().get(user.getLastBranch().getId()).getPastPeriodClosingDate() != null) {
            if (date.before(
                    getLastBranchSetting().getPastPeriodClosingDate())) {
                FacesMessage message = new FacesMessage();

                message.setSeverity(FacesMessage.SEVERITY_ERROR);
                message.setSummary(loc.getString("error"));
                message.setDetail(loc.getString("perioderror") + StaticMethods.convertToDateFormatWithSeconds(user.getLastBranch().getDateFormat(), getLastBranchSetting().getPastPeriodClosingDate()));

                FacesContext.getCurrentInstance().addMessage(null, message);
                RequestContext context = RequestContext.getCurrentInstance();
                context.update("grwProcessMessage");

                return false;
            }
        }

        return true;
    }

}
