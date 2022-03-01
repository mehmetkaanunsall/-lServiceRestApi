/**
 *
 *
 *
 * @author Ali Kurt
 *
 * @date 09.03.2018 09:04:12
 */
package com.mepsan.marwiz.general.report.bankextract.presentation;

import com.mepsan.marwiz.general.model.finance.Bank;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("bankConverter")
public class BankConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null) {
            return null;
        }
        BankExtractBean data = fc.getApplication().evaluateExpressionGet(fc, "#{bankExtractBean}", BankExtractBean.class);
//        for (Bank b : data.getListOfBank()) {
//            if (b.getId()==Integer.parseInt(string)) {
//                return b;
//            }
//        }
        throw new ConverterException(new FacesMessage(String.format("Cannot convert %s to BanAccount", string)));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof Bank) {
            Bank s = (Bank) o;
            String name = ""+s.getId();
            return name;
        } else {
            throw new ConverterException(new FacesMessage(o + " is not a valid BanAccount"));
        }
    }

}
