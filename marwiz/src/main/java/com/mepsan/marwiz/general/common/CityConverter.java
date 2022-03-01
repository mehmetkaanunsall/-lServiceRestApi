/**
 * This class ...
 *
 *
 * @author Esra Çabuk
 *
 * @date   21.03.2017 07:24:43
 */

package com.mepsan.marwiz.general.common;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import com.mepsan.marwiz.general.model.system.City;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("cityConverter")
public class CityConverter implements Converter{     
     
    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
         if (string == null) {
            return null;
        }
        CitiesAndCountiesBean data = fc.getApplication().evaluateExpressionGet(fc, "#{citiesAndCountiesBean}", CitiesAndCountiesBean.class);
        SessionBean data2 = fc.getApplication().evaluateExpressionGet(fc, "#{sessionBean}", SessionBean.class);
        for (City s : data.getCities()) {
            if (s.getName(data2.getLangId()).equals(string)) {
                return s;
            }
        }
        throw new ConverterException(new FacesMessage(String.format("Cannot convert %s to City", string)));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
         if (o == null) {
            return "";
        }
        if (o instanceof City) {
            SessionBean data2 = fc.getApplication().evaluateExpressionGet(fc, "#{sessionBean}", SessionBean.class);
            City s = (City) o;
            String name = s.getName(data2.getLangId());
            return name;
        } else {
            throw new ConverterException(new FacesMessage(o + " is not a valid city"));
        }
    }

}
