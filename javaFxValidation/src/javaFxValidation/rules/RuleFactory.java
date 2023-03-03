package javaFxValidation.rules;

import java.util.ArrayList;
import java.util.Arrays;

import javaFxValidation.Field;
import javaFxValidation.ValidationException;

public class RuleFactory {
	
	protected static final String RULES_SEPARATOR 		= "\\|"; 
	protected static final String RULE_PARAMS_SEPARATOR = ":"; 
	protected static final String PARAMS_SEPARATOR 		= ",";
	
	
	private static ArrayList<String> validExplicitRules = new ArrayList<>(Arrays.asList(
			"required",
			"notEmpty",
			"alpha",
			"alphaNumeric",
			"alphaDash",
			"numeric",
			"email",
			"date",
			"boolean"
	));
	
	private static ArrayList<String> validParameterizedRules = new ArrayList<>(Arrays.asList(
			"digits",
			"between",
			"in",
			"notIn",
			"size",
			"max",
			"min",
			"digits_max",
			"digits_min",
			"length",
			"length_max",
			"length_min",
			"gt",
			"gte",
			"lt",
			"lte",
			"equal",
			"mime",
			"format",
			"regex",
			"same"
			
	));
	
	protected static ArrayList<String> rulesOfOneParameter = new ArrayList<>(Arrays.asList(
			"digits",
			"max",
			"min",
			"digits_max",
			"digits_min",
			"length",
			"length_max",
			"length_min",
			"gt",
			"gte",
			"lt",
			"lte",
			"equal",
			"format",
			"regex",
			"same"
	));
	
	protected static ArrayList<String> rulesOfTwoParameters = new ArrayList<>(Arrays.asList(
			"between"
	));
	
	protected static ArrayList<String> rulesOfMoreThanTwoParameters = new ArrayList<>(Arrays.asList(
			"in",
			"notIn",
			"mime"
	));
	
	/**
	 * The field that rules must be generated for.
	 */
	private Field field;
	
	public void generateRulesForField(Field field) throws ValidationException {
		
		this.field = field;

		parse();
	}
	
	private void parse() throws ValidationException {
		String[] pasedRules = field.getRulesString().split(RULES_SEPARATOR);

		for (String rule : pasedRules) {
			if(rule.contains(RULE_PARAMS_SEPARATOR))
				field.addRule(parseParameterizedRule(rule));
			else
				field.addRule(parseExplicitRule(rule));
		}
	}
	
	private ExplicitRule parseExplicitRule(String ruleName) throws ValidationException {
		
		if(!validExplicitRules.contains(ruleName))
			throw new ValidationException("Rule " + ruleName + " is not valid explicit rule.");
		if(ruleName.equals("required"))
			field.setRequired();
		return new ExplicitRule(ruleName);
	}
	
	private ParameterizedRule parseParameterizedRule(String ruleNameWithParams) throws ValidationException {
		// We separate rule from its parameters. example [digits:5] [between:20,100]
		String[] ruleWithParams = ruleNameWithParams.split(RULE_PARAMS_SEPARATOR);
		
		if(!validParameterizedRules.contains(ruleWithParams[0]))
			throw new ValidationException("Rule " + ruleWithParams[0] + " is not valid parameterized rule.");
		
		if(ruleWithParams[0].equals("required"))
			field.setRequired();
		
		ParameterizedRule paramRule = new ParameterizedRule(ruleWithParams[0]);

		paramRule.setParameters(parseParameters(ruleWithParams[1]));
		
		return paramRule;
	}
	
	
	/**
	 * Make the parameters part as an arrayList
	 * @param parameters
	 * @return
	 */
	private ArrayList<String> parseParameters(String parameters) {
		
		ArrayList<String> params = new ArrayList<>();
		
		for (String string : parameters.split(PARAMS_SEPARATOR)) {
			params.add(string);
		}
		return params;
	}
}
