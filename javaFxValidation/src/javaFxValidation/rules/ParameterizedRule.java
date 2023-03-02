package javaFxValidation.rules;

import java.util.ArrayList;

import javaFxValidation.Regex;
import javaFxValidation.Str;
import javaFxValidation.ValidationException;

public class ParameterizedRule extends Rule {

	/**
	 * Parameterized rules are rules that have parameters like:
	 * max:40 min:30       ---> max is a rule. 40 is a parameter.
	 * digits:10           ---> digits is a rule. 10 is a parameter.
	 * in:admin,student    ---> in is a rule. admin and student are parameters.
	 */
	protected ArrayList<String> parameters = new ArrayList<>();
	
	/**
	 * @param fieldName      The field under validation.
	 * @param fieldValue     The value of the field under validation.
	 * @param name           The rule this field must pass.
	 * @throws ValidationException
	 */
	public ParameterizedRule(String name) throws ValidationException {
		super(name);
		
		setMatcher(parseMatcher(getName()));
	}


	public ArrayList<String> getParameters() {
		return parameters;
	}

	public void setParameters(ArrayList<String> parameters) {
		this.parameters = parameters;
	}
	
	public void addParameter(String param) {
		parameters.add(param);
	}
	
	public void addParameters(ArrayList<String> params) {
		parameters.addAll(params);
	}
	
	/**
	 * Here we generate Lambda function for a rule to match a field value.
	 * @param rule
	 * @return Lambda Function to matches a value for given rule.
	 * @throws ValidationException
	 */
	protected Matcher parseMatcher(String rule) throws ValidationException {
		switch (rule) {
		case "digits": {
			return digits();
		}
		case "between": {
			return matchBetween();
		}
		case "max": {
			return matchMax();
		}
		case "min": {
			return matchMin();
		}
		case "digits_max": {
			return matchLength("<=");
		}
		case "digits_min": {
			return matchLength(">=");
		}
		case "length": {
			return matchLength("=");
		}
		case "length_max": {
			return matchLength("<=");
		}
		case "length_min": {
			return matchLength(">=");
		}
		case "in": {
			return value -> getParameters().contains(value);
		}
		case "notIn": {
			return value -> !getParameters().contains(value);
		}
		case "gt": {
			return matchCompareValue(">");
		}
		case "gte": {
			return matchCompareValue(">=");
		}
		case "lt": {
			return matchCompareValue("<");
		}
		case "lte": {
			return matchCompareValue("<=");
		}
		case "format": {
			return value -> Str.isDate(value, getParameters().get(0));
		}
		case "regex": {
			return string -> Regex.matches(string, getParameters().get(0));
		}
		case "same": {
			return string -> false;
		}
		default:
			throw new ValidationException("Could not generate rule: " + rule);
		}
	}
	
	private Matcher digits() throws ValidationException {
		return value -> {
			if(!Str.isNumeric(value))
				return false;
			
			return value.length() == Integer.parseInt(getParameters().get(0)) ? true : false;
		};
	}
	
	private Matcher matchBetween() throws ValidationException {
		return value->{
			try {
				long fValue = Long.parseLong(value);
				long param1 = Long.parseLong(getParameters().get(0));
				long param2 = Long.parseLong(getParameters().get(1));
				
				if(fValue >= param1 && fValue <= param2)
					return true;
				
				return false;
			} catch (Exception e) {
				return false;
			}
		};
	}
	
	private Matcher matchMax() throws ValidationException {
		return value->{
			// if the value is number so max is value.
			if(Str.isNumeric(value))
				return Long.parseLong(value) < Long.parseLong(getParameters().get(0)) ? true : false;
			// otherwise Max is a length of the value
			return value.length() < Integer.parseInt(getParameters().get(0)) ? true : false;
		};
	}
	
	private Matcher matchMin() throws ValidationException {
		return value->{
			// if the value is number so min is value.
			if(Str.isNumeric(value))
				return Long.parseLong(value) > Long.parseLong(getParameters().get(0)) ? true : false;
			// otherwise Min is a length of the value
			return value.length() > Integer.parseInt(getParameters().get(0)) ? true : false;
		};
	}
	
	private Matcher matchLength(String operator) throws ValidationException {
		return value->{
			if(operator.equals(">="))
				return value.length() >= Integer.parseInt(getParameters().get(0)) ? true : false;
			else if(operator.equals("<="))
				return value.length() <= Integer.parseInt(getParameters().get(0)) ? true : false;
			else if(operator.equals("=="))
				return value.length() == Integer.parseInt(getParameters().get(0)) ? true : false;
			
			return false;
		};
	}
	
	private Matcher matchCompareValue(String operator) throws ValidationException {
		return value->{
			try {
				Double v = Double.parseDouble(value);
				Double param = Double.parseDouble(getParameters().get(0));
				if(operator.equals(">="))
					return v >= param ? true : false;
				else if(operator.equals("<="))
					return v <= param ? true : false;
				else if(operator.equals("<"))
					return v < param ? true : false;
				else if(operator.equals(">"))
					return v > param ? true : false;
				else if(operator.equals("=="))
						return v == param ? true : false;
				
			} catch (Exception e) {
				return false;
			}
			return false;
		};
	}
}
