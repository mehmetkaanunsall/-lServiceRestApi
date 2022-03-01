/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.07.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.system;

import com.mepsan.marwiz.general.model.wot.Dictionary;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Size;

public class City extends WotLogging{

    private int id;
    @Size(max = 50)
    private String tag;
    @Size(max = 20)
    private String telephoneCode;
    @Size(max = 10)
    private String plateCode;
    private Country country;
    private List<County> listOfCounty;
    private Map<Integer, Dictionary<City>> nameMap;

    public City() {
        this.country = new Country();
        this.listOfCounty = new ArrayList<>();
        this.nameMap = new HashMap<>();
    }

    public City(int id) {
        this.id = id;
    }

    public City(int id, Country country) {
        this.id = id;
        this.country = country;
    }

    public List<County> getListOfCounty() {
        return listOfCounty;
    }

    public void setListOfCounty(List<County> listOfCounty) {
        this.listOfCounty = listOfCounty;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTelephoneCode() {
        return telephoneCode;
    }

    public void setTelephoneCode(String telephoneCode) {
        this.telephoneCode = telephoneCode;
    }

    public String getPlateCode() {
        return plateCode;
    }

    public void setPlateCode(String plateCode) {
        this.plateCode = plateCode;
    }

    public String getName(int langId) {
        return nameMap.get(langId).getName();
    }

    public Map<Integer, Dictionary<City>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<City>> nameMap) {
        this.nameMap = nameMap;
    }

   

    @Override
    public String toString() {
        return this.getTag();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
