/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.05.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.wot.WotLogging;
import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Language;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.Authentication;
import java.util.ArrayList;
import java.util.List;

public class UserData extends WotLogging {

    private int id;
    private String username;
    private String password;
    private String name;
    private String surname;
    private String phone;
    private String mail;
    private String address;
    private boolean isRightNumeric;
    private boolean isMobile;
    private boolean isAdmin;

    private Authorize lastAuthorize;
    private City city;
    private Country country;
    private County county;
    private Language language;
    private Status status;
    private Type type;
    private Branch lastBranch;
    private BranchSetting lastBranchSetting;
    private String gridRowSelect;

    private String lastTheme;
    private String gridSize;
    private List<Integer> authoriedPages;
    private List<Branch> authorizedBranches;
    private Account account;
    private List<Authentication> authentications;
    private boolean isAuthorized;
    private boolean isCashierAddSalesBasket;
    private String mposPages;
    
    


    //---Authentication --------
    public UserData() {

        this.lastBranch = new Branch();
        this.lastAuthorize = new Authorize();
        this.city = new City();
        this.country = new Country();
        this.county = new County();
        this.language = new Language();
        this.status = new Status();
        this.type = new Type();
        this.account = new Account();
        this.authentications = new ArrayList<>();
    }

    
    public List<Authentication> getAuthentications() {
        return authentications;
    }

    public void setAuthentications(List<Authentication> authentications) {
        this.authentications = authentications;
    }

    public BranchSetting getLastBranchSetting() {
        return lastBranchSetting;
    }

    public void setLastBranchSetting(BranchSetting lastBranchSetting) {
        this.lastBranchSetting = lastBranchSetting;
    }

    public UserData(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isIsRightNumeric() {
        return isRightNumeric;
    }

    public void setIsRightNumeric(boolean isRightNumeric) {
        this.isRightNumeric = isRightNumeric;
    }

    public Authorize getLastAuthorize() {
        return lastAuthorize;
    }

    public void setLastAuthorize(Authorize lastAuthorize) {
        this.lastAuthorize = lastAuthorize;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Integer> getAuthoriedPages() {
        return authoriedPages;
    }

    public void setAuthoriedPages(List<Integer> authoriedPages) {
        this.authoriedPages = authoriedPages;
    }

    public boolean isIsMobile() {
        return isMobile;
    }

    public void setIsMobile(boolean isMobile) {
        this.isMobile = isMobile;
    }

    public boolean isIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(boolean isAdmin) {
        this.isAdmin = isAdmin;
    }

    public String getLastTheme() {
        return lastTheme;
    }

    public void setLastTheme(String lastTheme) {
        this.lastTheme = lastTheme;
    }

    public String getGridSize() {
        return gridSize;
    }

    public void setGridSize(String gridSize) {
        this.gridSize = gridSize;
    }

    public Branch getLastBranch() {
        return lastBranch;
    }

    public void setLastBranch(Branch lastBranch) {
        this.lastBranch = lastBranch;
    }

    public String getGridRowSelect() {
        return gridRowSelect;
    }

    public void setGridRowSelect(String gridRowSelect) {
        this.gridRowSelect = gridRowSelect;
    }

    public List<Branch> getAuthorizedBranches() {
        return authorizedBranches;
    }

    public void setAuthorizedBranches(List<Branch> authorizedBranches) {
        this.authorizedBranches = authorizedBranches;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public boolean isIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(boolean isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    public boolean isIsCashierAddSalesBasket() {
        return isCashierAddSalesBasket;
    }

    public void setIsCashierAddSalesBasket(boolean isCashierAddSalesBasket) {
        this.isCashierAddSalesBasket = isCashierAddSalesBasket;
    }

    public String getMposPages() {
        return mposPages;
    }

    public void setMposPages(String mposPages) {
        this.mposPages = mposPages;
    }

    @Override
    public String toString() {
        return this.getFullName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

    public String getDefualtGridSize() {
        if (gridSize != null) {
            return gridSize.split(",")[0];
        }
        return null;
    }

    public String getFullName() {

        return this.getName() + " " + this.getSurname();

    }

}
