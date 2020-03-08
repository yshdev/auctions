/************************************************************
 * ConfirmValidator.java
 * 
 * Validator class for JSF page "register.xhtml" to compare
 * fields of "password" and "confirm password".
 ************************************************************/
package project.auctionserver;

import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.faces.context.FacesContext;
import javax.faces.component.UIComponent;
import javax.faces.application.FacesMessage;

@FacesValidator(value="confirmValidator")
public class ConfirmValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        Object otherValue = component.getAttributes().get("otherValue");

        if (value == null || otherValue == null) {
            return; // if one of fields is empty let requiredMessage appear
        }

        if (!value.equals(otherValue)) {
            throw new ValidatorException(new FacesMessage("Values are not equal."));
        }
    }

}
