/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   25.05.2016 18:02:07
 */
package com.mepsan.marwiz.general.model.system;

import com.mepsan.marwiz.general.model.wot.Dictionary;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.Size;

public class Country extends WotLogging {

    private int id;
    @Size(max = 4)
    private String code;
    @Size(max = 50)
    private String tag;
    @Size(max = 10)
    private String telephoneCode;
    @Size(max = 25)
    private String latitude;
    @Size(max = 25)
    private String longitude;
    private int zoom;
    private List<City> listOfCities;
    private Map<Integer, Dictionary<Country>> nameMap;

//    public Country(int id) {
//        this.id = id;
//       // this.tag = tag;
//    }
    public Country() {
        this.listOfCities = new ArrayList<>();
        nameMap = new HashMap<>();
    }

    public Country(int id, String code, String latitude, String longitude, int zoom) {
        this.id = id;
        this.code = code;
        //   this.tag = tag;
        this.latitude = latitude;
        this.longitude = longitude;
        this.zoom = zoom;
    }

    public Country(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        if (code != null) {
            return code.trim();
        }
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTag() {
        return tag;
    }

    public String getTelephoneCode() {
        return telephoneCode;
    }

    public void setTelephoneCode(String telephoneCode) {
        this.telephoneCode = telephoneCode;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getName(int langId) {
        return nameMap.get(langId).getName();
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

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        this.zoom = zoom;
    }

    public List<City> getListOfCities() {
        return listOfCities;
    }

    public void setListOfCities(List<City> listOfCities) {
        this.listOfCities = listOfCities;
    }

    public Map<Integer, Dictionary<Country>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<Country>> nameMap) {
        this.nameMap = nameMap;
    }

    @Override
    public String toString() {
        return this.getCode();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
