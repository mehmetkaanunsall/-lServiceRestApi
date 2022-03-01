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
import java.util.Map;

public class Language extends WotLogging{

    private int id;
    private String code;
    private String tag;
    private Map<Integer, Dictionary<Language>> nameMap;

    public Language() {
    }

    public Language(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Map<Integer, Dictionary<Language>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<Language>> nameMap) {
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
