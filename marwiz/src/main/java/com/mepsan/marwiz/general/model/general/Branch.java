/**
 *
 * @author SALİM VELA ABDULHADİ
 *
 * Jan 22, 2018 11:25:50 AM
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;
import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.system.Language;
import com.mepsan.marwiz.general.model.system.Status;
import com.mepsan.marwiz.general.model.system.Type;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.ArrayList;
import java.util.List;

public class Branch extends WotLogging {

    private int id;
    private String name;
    private String title;
    private String licenceCode;
    private String taxNo;
    private String taxOffice;
    private Status status;
    private String latitude;
    private String longitude;
    private Type type;
    private Currency currency;
    private int currencyrounding;
    private String dateFormat;
    private int decimalsymbol;
    private String phone;
    private String mail;
    private String address;
    private String mobile;
    private Language language;
    private City city;
    private Country country;
    private County county;
    private String mersisNo;
    private String webAddress;
    private String tradeRegisterNo;
    private boolean isCentral;
    private List<Authorize> listOfAuthorizes;
    private boolean isCenterInteg;
    private boolean isAgency;
    private boolean isLicenceCodeCheck;
    private int conceptType;
    private boolean isTakeAway;
    private boolean isVehicleDelivery;
    private int specialItem; // Araca Teslim ile ilgili checkboxlar için 
    private boolean isCentralIntegration;

    public Branch() {

        this.status = new Status();
        this.type = new Type();
        this.currency = new Currency();
        this.language = new Language();
        this.city = new City();
        this.country = new Country();
        this.county = new County();
        this.listOfAuthorizes = new ArrayList<>();
    }

    public Branch(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLicenceCode() {
        return licenceCode;
    }

    public void setLicenceCode(String licenceCode) {
        this.licenceCode = licenceCode;
    }

    public String getTaxNo() {
        return taxNo;
    }

    public void setTaxNo(String taxNo) {
        this.taxNo = taxNo;
    }

    public String getTaxOffice() {
        return taxOffice;
    }

    public void setTaxOffice(String taxOffice) {
        this.taxOffice = taxOffice;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public int getCurrencyrounding() {
        return currencyrounding;
    }

    public void setCurrencyrounding(int currencyrounding) {
        this.currencyrounding = currencyrounding;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public int getDecimalsymbol() {
        return decimalsymbol;
    }

    public void setDecimalsymbol(int decimalsymbol) {
        this.decimalsymbol = decimalsymbol;
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

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
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

    public List<Authorize> getListOfAuthorizes() {
        return listOfAuthorizes;
    }

    public void setListOfAuthorizes(List<Authorize> listOfAuthorizes) {
        this.listOfAuthorizes = listOfAuthorizes;
    }

    public String getMersisNo() {
        return mersisNo;
    }

    public void setMersisNo(String mersisNo) {
        this.mersisNo = mersisNo;
    }

    public String getWebAddress() {
        return webAddress;
    }

    public void setWebAddress(String webAddress) {
        this.webAddress = webAddress;
    }

    public String getTradeRegisterNo() {
        return tradeRegisterNo;
    }

    public void setTradeRegisterNo(String tradeRegisterNo) {
        this.tradeRegisterNo = tradeRegisterNo;
    }

    public boolean isIsCentral() {
        return isCentral;
    }

    public void setIsCentral(boolean isCentral) {
        this.isCentral = isCentral;
    }

    public boolean isIsCenterInteg() {
        return isCenterInteg;
    }

    public void setIsCenterInteg(boolean isCenterInteg) {
        this.isCenterInteg = isCenterInteg;
    }

    public boolean isIsAgency() {
        return isAgency;
    }

    public void setIsAgency(boolean isAgency) {
        this.isAgency = isAgency;
    }

    public boolean isIsLicenceCodeCheck() {
        return isLicenceCodeCheck;
    }

    public void setIsLicenceCodeCheck(boolean isLicenceCodeCheck) {
        this.isLicenceCodeCheck = isLicenceCodeCheck;
    }

    public int getConceptType() {
        return conceptType;
    }

    public void setConceptType(int conceptType) {
        this.conceptType = conceptType;
    }

    public boolean isIsTakeAway() {
        return isTakeAway;
    }

    public void setIsTakeAway(boolean isTakeAway) {
        this.isTakeAway = isTakeAway;
    }

    public boolean isIsVehicleDelivery() {
        return isVehicleDelivery;
    }

    public void setIsVehicleDelivery(boolean isVehicleDelivery) {
        this.isVehicleDelivery = isVehicleDelivery;
    }

    public int getSpecialItem() {
        return specialItem;
    }

    public void setSpecialItem(int specialItem) {
        this.specialItem = specialItem;
    }

    public boolean isIsCentralIntegration() {
        return isCentralIntegration;
    }

    public void setIsCentralIntegration(boolean isCentralIntegration) {
        this.isCentralIntegration = isCentralIntegration;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
