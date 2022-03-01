/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   05.10.2016 14:57:13
 */
package com.mepsan.marwiz.general.model.wot;

import com.mepsan.marwiz.general.model.system.Country;
import com.mepsan.marwiz.general.model.system.Type;
import javax.validation.constraints.Size;

public class Phone<T> extends WotLogging {

    private int id;
    private Type phoneType;
    @Size(max = 50)
    private String tag;
    private T Object;
    private Country country;
    private boolean defaultValue;

    public Phone() {
        this.country = new Country();
        this.phoneType = new Type();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getPhoneType() {
        return phoneType;
    }

    public void setPhoneType(Type phoneType) {
        this.phoneType = phoneType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public T getObject() {
        return Object;
    }

    public void setObject(T Object) {
        this.Object = Object;
    }

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public boolean isDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
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
