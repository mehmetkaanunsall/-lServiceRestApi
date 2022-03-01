/**
 * Bu Sınıf ...
 *
 *
 * @author Salem Walaa Abdulhadie
 *
 * @date   05.10.2016 14:57:01
 */
package com.mepsan.marwiz.general.model.wot;

import com.mepsan.marwiz.general.model.system.Type;
import javax.validation.constraints.Size;

public class Internet<T> extends WotLogging {

    private int id;
    private Type internetType;
    @Size(max = 50)
    private String tag;
    private T object;
    private boolean defaultValue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Type getInternetType() {
        return internetType;
    }

    public void setInternetType(Type internetType) {
        this.internetType = internetType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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
        return this.getTag();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }
}
