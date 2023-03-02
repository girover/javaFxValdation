package javaFxValidation.rules;

import java.util.HashMap;

public class Messages {

	private static HashMap<String, String> explicitRulesMessages = null;
	private static HashMap<String, String> parameterizedRulesMessages = null;

	private static HashMap<String, String> getExplicitRulesMessages() {
		if (explicitRulesMessages == null) {

			explicitRulesMessages = new HashMap<>();
			// Explicit Rules Error Messages
			explicitRulesMessages.put("required", "The %s field is required.");
			explicitRulesMessages.put("notEmpty", "The %s field can not be empty.");
			explicitRulesMessages.put("email", "The %s must be a valid email address.");
			explicitRulesMessages.put("alpha", "The %s must only contain letters.");
			explicitRulesMessages.put("alphaDash", "The %s must only contain letters, numbers, dashes and underscores.");
			explicitRulesMessages.put("alphaNumeric", "The %s must only contain letters and numbers.");
			explicitRulesMessages.put("uppercase", "The %s must be uppercase.");
			explicitRulesMessages.put("lowercase", "The %s must be lowercase.");
			explicitRulesMessages.put("numeric", "The %s must be a number.");
			explicitRulesMessages.put("boolean", "The %s field must be true or false.");
			explicitRulesMessages.put("date", "The %s is not a valid date.");
		}
		return explicitRulesMessages;
	}

	private static HashMap<String, String> getParameterizedRulesMessages() {

		if (parameterizedRulesMessages == null) {
			
			parameterizedRulesMessages = new HashMap<>();
			// Parameterized Rules Error Messages
			parameterizedRulesMessages.put("digits", "The %s must be %s digits."); // CPR digits:8
			parameterizedRulesMessages.put("between", "The %s must be between %s and %s."); // age between:5,50
			parameterizedRulesMessages.put("in", "The %s must be in %s."); // role in:admin,user
			parameterizedRulesMessages.put("notIn", "The %s cannot be in %s."); // role in:admin,user
			parameterizedRulesMessages.put("max", "The %s must not be greater than %s."); // age max:100
			parameterizedRulesMessages.put("min", "The %s must be at least %s."); // age min:10
			parameterizedRulesMessages.put("digits_max", "The %s must not have more than %s digits.");
			parameterizedRulesMessages.put("digits_min", "The %s must have at least %s digits.");
			parameterizedRulesMessages.put("length", "The length of %s must be %s.");
			parameterizedRulesMessages.put("length_max", "The length of %s must not be longer than %s.");
			parameterizedRulesMessages.put("length_min", "The length of %s must not be shorter than %s.");
			parameterizedRulesMessages.put("format", "The date format of %s must be %s.");
			parameterizedRulesMessages.put("gt", "The %s must be greater than %s.");
			parameterizedRulesMessages.put("lt", "The %s must be less than %s.");
			parameterizedRulesMessages.put("gte", "The %s must be greater than or equal to %s.");
			parameterizedRulesMessages.put("lte", "The %s must be less than or equal to %s.");
			parameterizedRulesMessages.put("regex", "The %s not matches the Regular Expression %s.");
			parameterizedRulesMessages.put("mime", "The %s accepts only extentions: %s."); // email same:confirmation
			parameterizedRulesMessages.put("same", "The %s doesn't match %s."); // email same:confirmation
		}
		
		return parameterizedRulesMessages;
	}

	public static String getExplicitRuleMessage(String rule) {
		return getExplicitRulesMessages().get(rule);
	}

	public static String getParameterizedRuleMessage(String rule) {
		return getParameterizedRulesMessages().get(rule);
	}
}
