package javaFxValidation;

import java.time.LocalDate;
import java.util.ArrayList;

import javaFxValidation.annotations.Msg;
import javaFxValidation.annotations.Rules;
import javaFxValidation.rules.ExplicitRule;
import javaFxValidation.rules.Messages;
import javaFxValidation.rules.ParameterizedRule;
import javaFxValidation.rules.Rule;
import javaFxValidation.rules.RuleMessage;
import javaFxValidation.rules.RuleMessagesBag;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DatePicker;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.ToggleButton;

public class Validator {

	/**
	 * We use reflection to get all fields of this controller.
	 * Only fields annotated by 'Validate' will be validated
	 */
	private Object controllerUnderValidation;
	private RuleMessagesBag messagesBag;
	
	private ArrayList<Field> fieldsUnderValidation;
	private ArrayList<Field> failedFields;
	/**
	 * If only some of annotated field will be validated.
	 * These fields pass to the method validate(String[]...fieldNames)
	 */
	private ArrayList<String> selectedFields;
	
	public Validator(Object controller) throws ValidationException {
		controllerUnderValidation = controller;
		messagesBag  = new RuleMessagesBag();
		fieldsUnderValidation = new ArrayList<>();
		failedFields = new ArrayList<>();
		selectedFields = new ArrayList<>();
		
	}
	
	public void validate() throws ValidationException {
		makeFields();
	}

	public void validate(String...fields) throws ValidationException {
		for (String fieldName : fields) {
			selectedFields.add(fieldName);
		}
		
		validate();
	}

	/**
	 * Determine if all specified rules passes the user inputs
	 * @param boolean When one field fails to pass one rule, it will stop checking more.
	 * @return boolean
	 * @throws ValidationException
	 */
	public boolean passes(boolean stopOnFirstFailure) throws ValidationException {
	
		passFieldsUnderValidation(stopOnFirstFailure);
		
		mergeMessages();
		
		return failedFields.size() > 0 ? false : true;
	}

	/**
	 * As default, the validator will check all the rules for a field
	 * under validation despite all failures.
	 * @return
	 * @throws ValidationException
	 */
	public boolean passes() throws ValidationException {
		return passes(false);
	}

	/**
	 * Retrieve all fields under validation.
	 * @return
	 */
	public ArrayList<Field> getFieldsUnderValidation() {
		return fieldsUnderValidation;
	}
	
	public ArrayList<Field> getNotPassedFields(){
		return failedFields;
	}

	/**
	 * Retrieve all error messages from validator for failed rules.
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getErrorMessages() {
		
		ArrayList<String> errorMessages = new ArrayList<>();
		
		for (Field field : failedFields)
			for (Rule rule : field.getFailedRules()) {
				errorMessages.add(rule.getMessage());
			}
		
		return errorMessages;
	}

	/**
	 * Retrieve all error messages from validator for a specific field.
	 * @return ArrayList<String>
	 */
	public ArrayList<String> getErrorMessagesFor(String fieldName) {
		
		ArrayList<String> errorMessages = new ArrayList<>();
		
		Field failedField = getNotPassedField(fieldName);
		
		if(failedField != null)
			for (Rule rule : failedField.getFailedRules()) {
				errorMessages.add(rule.getMessage());
			}
		
		return errorMessages;
	}

	/**
	 * Get field under validation by its name.
	 * 
	 * @param fieldName
	 * @return
	 */
	private Field getFieldUnderValidationByName(String fieldName) {
		for (Field field : fieldsUnderValidation) {
			if(field.getName().equals(fieldName))
				return field;
		}
		return null;
	}

	private Field getNotPassedField(String fieldName){
		for (Field field : failedFields) {
			if(field.getName().equals(fieldName))
				return field;
		}
		
		return null;
	}
	
	/**
	 * Add rules for the given field to validate the given value.
	 * @param fieldName
	 * @param fieldValue
	 * @param rules
	 * @throws ValidationException
	 */
	private void addFieldRules(String fieldName, String fieldValue, String rules) throws ValidationException {
		
		if(fieldName == null || fieldName.isBlank() || rules == null || rules.isBlank())
			throw new ValidationException("Bad arguments: field name and rules can not be null or empty");
		
		fieldsUnderValidation.add(new Field(fieldName, fieldValue, rules));
	}
	
	private void addRuleMessage(String fieldName, String ruleName, String message) {
		/**
		 * If the rule and message already exist for the given field,
		 * we will only change the message.
		 */
		RuleMessage m = messagesBag.get(fieldName, ruleName);
		if(m != null) {
			m.setMessage(message);
			return;
		}
		
		/**
		 * Otherwise we will add the new RuleMessage to the RuleMessagesBag
		 */
		messagesBag.add(new RuleMessage(fieldName, ruleName, message));
	}
	
	private void makeFields() throws ValidationException {
		for (java.lang.reflect.Field field : controllerUnderValidation.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Rules.class)) {
				try {
					
					field.setAccessible(true);
					Rules rulesAnnotation = field.getAnnotation(Rules.class);
					String fieldName = rulesAnnotation.field();
					// If we need to validate some of annotated field
					if(selectedFields.size() > 0 && !selectedFields.contains(fieldName))
						continue;
					
					String fieldValue;
					if(isPrimitive(field.get(controllerUnderValidation)))
						fieldValue = (String)field.get(controllerUnderValidation);
					else {
						Control control = (Control)field.get(controllerUnderValidation);
						fieldValue = getValueOfJavaFxControl(control);
					}
					String rules = rulesAnnotation.rules();
					addFieldRules(fieldName, fieldValue, rules);
					parseMessagesFromAnnotation(fieldName, field);
				} catch (Exception e) {
					throw new ValidationException(e.getMessage());
				}
            }
        }
	}
	
	private boolean isPrimitive(Object object) {
		Class<?> cls = object.getClass();
		return (
				cls == String.class ||
				cls == Integer.class ||
				cls == Double.class ||
				cls == Float.class ||
				cls == Boolean.class ||
				cls == Long.class ||
				cls == Character.class || 
				cls == Byte.class || 
				cls == Short.class 
				)
				?true
				: false;
	}

	private void parseMessagesFromAnnotation(String fieldName, java.lang.reflect.Field field) {
		Msg[] messages = 
				field.getAnnotationsByType(Msg.class);
		for (Msg ruleMessage : messages) {
			String rule = ruleMessage.rule();
			String message = ruleMessage.message();
			addRuleMessage(fieldName, rule, message);
		}
	}

	private String getValueOfJavaFxControl(Control control) {
		if (control instanceof TextInputControl) {
	        return ((TextInputControl) control).getText();
	    } else if (control instanceof ComboBox) {
	        return ((ComboBox) control).getSelectionModel().getSelectedItem().toString();
	    } else if (control instanceof CheckBox) {
	        return ((CheckBox) control).isSelected() ? "true" : "false";
	    } else if (control instanceof ToggleButton) {
	        return ((ToggleButton) control).isSelected() ? "true" : "false";
	    } else if (control instanceof Spinner) {
	        return ((Spinner) control).getValue().toString();
	    } else if (control instanceof DatePicker) {
	        LocalDate date = ((DatePicker) control).getValue();
	        return date != null ? date.toString() : "";
	    } else if (control instanceof Slider) {
	        return String.valueOf(((Slider) control).getValue());
	    } else if (control instanceof TextArea) {
	        return ((TextArea) control).getText();
	    } else if (control instanceof ListView) {
	        ObservableList<?> selectedItems = ((ListView) control).getSelectionModel().getSelectedItems();
	        StringBuilder sb = new StringBuilder();
	        for (Object item : selectedItems) {
	            sb.append(item.toString()).append(", ");
	        }
	        if (sb.length() > 0) {
	            sb.setLength(sb.length() - 2); // remove the last comma and space
	        }
	        return sb.toString();
	    } else {
	        return null; // unsupported control type
	    }
	}

	private void passFieldsUnderValidation(boolean stopOnFirstFailure) throws ValidationException {

		for (Field field : fieldsUnderValidation) {
			/**
			 * Check if the field must be the same as another field.
			 */
			if(field.hasRule("same")) {
				Rule rule = field.getRuleByName("same");
				if(!(rule instanceof ParameterizedRule))
					throw new ValidationException("[same] rule has no parameters.");
				
				String fieldName = ((ParameterizedRule)rule).getParameters().get(0);

				field.setSameField(getFieldUnderValidationByName(fieldName));
			}
			
//			field.pass(stopOnFirstFailure);
			
			if(!field.passes(stopOnFirstFailure)) {
				failedFields.add(field);
				if(stopOnFirstFailure)
					break;
			}
		}
	}

	/**
	 * Here we retrieve error messages from the RuleMessagesBag, and inject them
	 * to the failed rules objects.
	 * If there are error messages of particular rules specified by the user,
	 * then we replace the original messages with the user's.
	 */
	private void mergeMessages() {

		for (Field field : failedFields)
			for (Rule rule : field.getFailedRules())
				if(messagesBag.contains(field.getName(), rule.getName()))
					rule.setMessage(messagesBag.get(field.getName(), rule.getName()).getMessage());
				else if(rule.getMessage() == null || rule.getMessage().isBlank()) {
					if(rule instanceof ExplicitRule) {
						String message = Messages.getExplicitRuleMessage(rule.getName());
						message = String.format(message, field.getName());
						rule.setMessage(message);
					}else if(rule instanceof ParameterizedRule) {
						String message = Messages.getParameterizedRuleMessage(rule.getName());
						message = String.format(message, getFiledNameAndParametersAsArray(field, (ParameterizedRule)rule));
						rule.setMessage(message);
					}
				}
	}

	private Object[] getFiledNameAndParametersAsArray(Field field, ParameterizedRule rule) {
		
		ArrayList<String> params = new ArrayList<>();
		params.add(field.getName());
		
		if (rule.getParameters().size() > 2)
			params.add(rule.getParameters().toString());
		else
			params.addAll(rule.getParameters());
		
		return params.toArray();
	}
}
