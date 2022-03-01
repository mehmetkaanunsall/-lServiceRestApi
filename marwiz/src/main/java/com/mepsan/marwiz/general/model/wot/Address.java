/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   05.10.2016 14:57:32
 */
package com.mepsan.marwiz.general.model.wot;

import com.mepsan.marwiz.general.model.system.City;
import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.County;

public class Address<T> extends WotLogging {

    private int id;
    private Country country;
    private City city;
    private County county;
    private String fulladdress;
    private T object;
    private boolean defaultValue;

    public Address() {
        this.country = new Country();
        this.city = new City();
        this.county = new County();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public County getCounty() {
        return county;
    }

    public void setCounty(County county) {
        this.county = county;
    }

    public String getFulladdress() {
        return fulladdress;
    }

    public void setFulladdress(String fulladdress) {
        this.fulladdress = fulladdress;
    }

    public T getObject() {
        return object;
    }

    public void setObject(T object) {
        this.object = object;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public String toString() {
        return this.getFulladdress();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
