/**
 * This class ...
 *
 *
 * @author Merve Karakarcayildiz
 *
 * @date   16.02.2018 05:40:40
 */
package com.mepsan.marwiz.general.report.orderlistreport.presentation;

import com.mepsan.marwiz.general.model.inventory.Warehouse;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

@FacesConverter("orderListReportWarehouseConverter")
public class OrderListReportWarehouseConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext fc, UIComponent uic, String string) {
        if (string == null) {
            return null;
        }
        OrderListReportBean data = fc.getApplication().evaluateExpressionGet(fc, "#{orderListReportBean}", OrderListReportBean.class);
        for (Warehouse s : data.getListOfWarehouse()) {
            if (s.getName().equals(string)) {
                return s;
            }
        }
        throw new ConverterException(new FacesMessage(String.format("Cannot convert %s to Warehouse", string)));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o == null) {
            return "";
        }
        if (o instanceof Warehouse) {
            Warehouse s = (Warehouse) o;
            String name = s.getName();
            return name;
        } else {
            throw new ConverterException(new FacesMessage(o + " is not a valid Warehouse"));
        }
    }
}
