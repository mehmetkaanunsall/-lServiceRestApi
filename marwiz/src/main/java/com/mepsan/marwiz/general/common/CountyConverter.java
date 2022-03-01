/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   21.03.2017 07:32:33
 */

package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.model.system.County;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("countyConverter")
public class CountyConverter implements Converter{

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
          if (string == null) {
            return null;
        }
        CitiesAndCountiesBean data = fc.getApplication().evaluateExpressionGet(fc, "#{citiesAndCountiesBean}", CitiesAndCountiesBean.class);
        for (County s : data.getCounties()) {
            if (s.getName().equals(string)) {
                return s;
            }
        }
        throw new ConverterException(new FacesMessage(String.format("Cannot convert %s to County", string)));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
         if (o == null) {
            return "";
        }
        if (o instanceof County) {
            County s = (County) o;
            String name = s.getName();
            return name;
        } else {
            throw new ConverterException(new FacesMessage(o + " is not a valid County"));
        }
    }

}
