package com.mepsan.marwiz.general.validation;

import com.mepsan.marwiz.general.core.presentation.SessionBean;
import java.util.Calendar;
import java.util.Date;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("dateRangeValidator")
public class DateRangeValidator implements Validator {

    @ManagedProperty(value = "#{sessionBean}") // session
    public SessionBean sessionBean;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value == null) {
            return;
        }

        //Leave the null handling of startDate to required="true"
        Object startDateValue = component.getAttributes().get("startDate");
        if (startDateValue == null) {
            return;
        }

        Date startDate = (Date) startDateValue;
        Date endDate = (Date) value;
        if (endDate.before(startDate)) {


            throw new ValidatorException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "", ""));
        }
    }

}
