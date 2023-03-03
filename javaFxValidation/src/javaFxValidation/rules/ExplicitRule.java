package javaFxValidation.rules;

import javaFxValidation.Str;
import javaFxValidation.ValidationException;

public class ExplicitRule extends Rule {

	/**
	 * @param fieldName      The field under validation.
	 * @param fieldValue     The value of the field under validation.
	 * @param name           The rule this field must pass.
	 * @throws ValidationException
	 */
	public ExplicitRule(String name) throws ValidationException {
		super(name);
		setMatcher(parseMatcher(name));
	}
	
	
	/**
	 * Here we generate Lambda function for a rule to check if the field value
	 * passes the given rule.
	 * @param rule
	 * @return Lambda Function to matches a value for given rule.
	 * @throws ValidationException
	 */
	protected Matcher parseMatcher(String rule) throws ValidationException {
		switch (rule) {
		case "required": {
			return string -> string != null && !string.isBlank();
		}
		case "notEmpty": {
			return string -> !Str.isEmpty(string);
		}
		case "alpha": {
			return string -> Str.isAlpha(string);
		}
		case "alphaNumeric": {
			return string -> Str.isAlphaNumeric(string);
		}
		case "alphaDash": {
			return string -> Str.isAlphaDash(string);
		}
		case "email": {
			return string -> Str.isEmail(string);
		}
		case "numeric": {
			return string -> Str.isNumeric(string);
		}
		case "date": {
			return string -> Str.isDate(string);
		}
		case "boolean": {
			return string -> Str.isBoolean(string);
		}
		default:
			throw new ValidationException("Could not generate rule: " + rule);
		}
	}
}
