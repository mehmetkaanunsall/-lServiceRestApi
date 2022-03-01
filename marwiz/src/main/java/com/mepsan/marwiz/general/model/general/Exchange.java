/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 17.02.2017 14:08:22
 */
package com.mepsan.marwiz.general.model.general;

import com.mepsan.marwiz.general.model.system.Currency;
import com.mepsan.marwiz.general.model.wot.WotLogging;
import java.math.BigDecimal;
import java.util.Date;

public class Exchange extends WotLogging {

    private int id;
    private Currency currency;
    private Currency responseCurrency;
    private Date exchangeDate;
    private BigDecimal buying;
    private BigDecimal sales;
    private String stringDate;
    private int isThereNowDate;
    private int errorCode;

    public Exchange() {
        this.currency = new Currency();
        this.responseCurrency = new Currency();
    }

    public Exchange(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getExchangeDate() {
        return exchangeDate;
    }

    public void setExchangeDate(Date exchangeDate) {
        this.exchangeDate = exchangeDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Currency getResponseCurrency() {
        return responseCurrency;
    }

    public void setResponseCurrency(Currency responseCurrency) {
        this.responseCurrency = responseCurrency;
    }

    public BigDecimal getBuying() {
        return buying;
    }

    public void setBuying(BigDecimal buying) {
        this.buying = buying;
    }

    public BigDecimal getSales() {
        return sales;
    }

    public void setSales(BigDecimal sales) {
        this.sales = sales;
    }

    public String getStringDate() {
        return stringDate;
    }

    public void setStringDate(String stringDate) {
        this.stringDate = stringDate;
    }

    public int getIsThereNowDate() {
        return isThereNowDate;
    }

    public void setIsThereNowDate(int isThereNowDate) {
        this.isThereNowDate = isThereNowDate;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    @Override
    public String toString() {
        return this.currency.getCode();
    }

    @Override
    public int hashCode() {
        return this.getId();
    }

}
