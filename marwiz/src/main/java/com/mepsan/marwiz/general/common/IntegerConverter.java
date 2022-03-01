/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   30.03.2017 03:55:00
 */
package com.mepsan.marwiz.general.common;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "integerConverter")
public class IntegerConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        int decimal=0;
       
        if (string != null) {
            if (string.equals("")) {
                decimal = 0;
            } else {
                try {
                    decimal = Integer.valueOf(string);
                } catch (Exception e) {
                }

            }
        }
        return decimal;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
        String decimal = "";
        if (object != null) {
            decimal = ((Integer) object).intValue()== 0d ? "" : object.toString();
        } else {
            decimal = "";
        }
        return decimal;

    }

}
