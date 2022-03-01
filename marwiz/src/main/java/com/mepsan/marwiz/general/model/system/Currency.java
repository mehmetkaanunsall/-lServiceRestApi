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
import java.math.BigDecimal;
import java.util.Map;

public class Currency extends WotLogging {

    private int id;
    private String code;
    private String tag;
    private String sign;
    private Map<Integer, Dictionary<Currency>> nameMap;
    private String internationalCode;
    private BigDecimal conversionRate;
    private BigDecimal limitUp;

    public Currency() {
    }

    public Currency(String internationalCode) {

        this.internationalCode = internationalCode;

    }

    public Currency(int id) {
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

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public Map<Integer, Dictionary<Currency>> getNameMap() {
        return nameMap;
    }

    public void setNameMap(Map<Integer, Dictionary<Currency>> nameMap) {
        this.nameMap = nameMap;
    }

    public String getInternationalCode() {
        return internationalCode;
    }

    public void setInternationalCode(String internationalCode) {
        this.internationalCode = internationalCode;
    }

    public BigDecimal getConversionRate() {
        return conversionRate;
    }

    public void setConversionRate(BigDecimal conversionRate) {
        this.conversionRate = conversionRate;
    }

    public BigDecimal getLimitUp() {
        return limitUp;
    }

    public void setLimitUp(BigDecimal limitUp) {
        this.limitUp = limitUp;
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
