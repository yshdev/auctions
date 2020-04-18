/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package project.auctionserver;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Shalom
 */
@FacesValidator("project.auctionserver.BlankFieldValidator")
public class BlankFieldValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        String label = component.getAttributes().get("label").toString();
        
        if (value == null) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", label + ": Validation Error: Value is required.");
            throw new ValidatorException(msg);
        }
        
        if ( value.toString().trim().isEmpty()) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Validation Error", label + ": Validation Error: Value cannot be empty.");
            throw new ValidatorException(msg);
        }
    }

}
