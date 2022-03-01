/**
 * Bu sınıf outputtext lerde parasal ifadeleri para birimiyle birlikte göstermek için yazılmıştır.
 *
 *
 * @author Ali Kurt
 *
 * @date   03.04.2017 17:55:57
 */
package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.appllication.presentation.ApplicationBean;
import com.mepsan.marwiz.general.core.presentation.Marwiz;
import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.general.Unit;
import com.mepsan.marwiz.general.model.system.Currency;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import org.springframework.beans.factory.annotation.Autowired;

@FacesConverter(value = "currencyConverter")
public class CurrencyConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {

        BigDecimal decimal = null;
        if (string != null) {
            if (string.equals("")) {
                decimal = null;
            } else {
                try {
                    decimal = new BigDecimal(string);
                } catch (Exception e) {
                }
            }
        }
        return decimal;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        Marwiz marwiz = fc.getApplication().evaluateExpressionGet(fc, "#{marwiz}", Marwiz.class);
        SessionBean sessionBean = fc.getApplication().evaluateExpressionGet(fc, "#{sessionBean}", SessionBean.class);
        String symbol = "";
        Unit unit = null;
        Currency currency = null;
        int currencyRounding = sessionBean.getUser().getLastBranch().getCurrencyrounding();//yuvarlama değeri

        String type = (String) uic.getAttributes().get("type");

        switch (type) {
            case "liter": //litre tipinde ise
                symbol = "LT";
                break;
            case "currency": //para tipinde ise
                currency = (Currency) uic.getAttributes().get("currency");
                symbol = sessionBean.currencySignOrCode(currency.getId(), 0);
                break;
            case "percent": //yüzde
                symbol = "%";
                break;
            case "sale": //stationuscsale den gelen kayıtlar için
                currency = new Currency();
                ApplicationBean applicationBean = fc.getApplication().evaluateExpressionGet(fc, "#{applicationBean}", ApplicationBean.class);
                currency.setId(Integer.parseInt(applicationBean.getParameterMap().get("stationuscsale_currency").getValue()));
                symbol = sessionBean.currencySignOrCode(currency.getId(), 0);
                break;
            case "unit": //litre olmayan birimler                
                unit = (Unit) uic.getAttributes().get("unit");
                if (unit.getSortName() != null) {
                    symbol = unit.getSortName();
                } else {
                    symbol = "";
                }
                currencyRounding = unit.getUnitRounding();
                break;
            default:
                symbol = "";
                break;
        }

        NumberFormat formatter = NumberFormat.getCurrencyInstance(sessionBean.getLocale());
        if (marwiz.getPageIdOfGoToPage() == 66 || marwiz.getPageIdOfGoToPage() == 104 || marwiz.getPageIdOfGoToPage() == 116 || marwiz.getOldId() == 66) {

            formatter.setMaximumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
            formatter.setMinimumFractionDigits(sessionBean.getUser().getLastBranchSetting().getShiftCurrencyRounding());
        } else {
            formatter.setMaximumFractionDigits(currencyRounding);
            formatter.setMinimumFractionDigits(currencyRounding);
        }

        formatter.setRoundingMode(RoundingMode.HALF_EVEN);
        DecimalFormatSymbols decimalFormatSymbols = ((DecimalFormat) formatter).getDecimalFormatSymbols();
        decimalFormatSymbols.setMonetaryDecimalSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? '.' : ',');
        decimalFormatSymbols.setGroupingSeparator(sessionBean.getUser().getLastBranch().getDecimalsymbol() == 1 ? ',' : '.');
        decimalFormatSymbols.setCurrencySymbol(symbol);
        ((DecimalFormat) formatter).setDecimalFormatSymbols(decimalFormatSymbols);

        String decimal = formatter.format(new BigDecimal(String.valueOf(object)));

        return decimal;

    }

}
