/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   21.12.2016 09:55:57
 */
package com.mepsan.marwiz.general.common;

import java.math.BigDecimal;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@FacesConverter(value = "doubleConverter")
public class DoubleConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        BigDecimal decimal = null;
        if (string != null) {
            if (string.equals("")) {
                decimal = null;
            } else {
                try {
                    Double valueOf = Double.valueOf(string);
                    decimal = BigDecimal.valueOf(valueOf);
                } catch (Exception e) {
                }
                
            }
        }
        return decimal;
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object object) {
       String decimal=""; 
       if(object!=null){ 
           decimal = ((BigDecimal)object).doubleValue() == 0d ? "" : object.toString();
       }else{ 
           decimal=""; 
       } return decimal;
       
       
      
    }

}
