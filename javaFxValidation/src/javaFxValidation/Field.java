package javaFxValidation;

import java.util.ArrayList;

import javaFxValidation.rules.Rule;
import javaFxValidation.rules.RuleFactory;

public class Field {

	private String name;
	private String value;
	private String rulesString;
	
	/**
	 *  Fields by default are optional. To make them required, provide rule 'required'
	 *  for instance: validator.addFieldRules("email" , "example@domain.com", "required|email");
	 */
	private boolean required = false;
	private boolean passedAllRules = false;
	private boolean hadBeenChecked = false;
	
	private Field sameField;
	
	private ArrayList<Rule> rules;
	private ArrayList<Rule> failedRules;
	
	
	public Field(String name, String value, String rules) throws ValidationException {
		
		this.name = name;
		this.value = value;
		this.rulesString = rules;
		
		this.rules = new ArrayList<>();
		this.failedRules = new ArrayList<>();
		
		makeFieldRules();
	}

	private void makeFieldRules() throws ValidationException {

		RuleFactory ruleFactory = new RuleFactory();
		
		ruleFactory.generateRulesForField(this);
	}
	
	public void setRequired() {
		required = true;
	}
	
	public boolean isRequired() {
		return required;
	}
	
	public Field getSameField() {
		return sameField;
	}

	public void setSameField(Field sameField) {
		this.sameField = sameField;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getRulesString() {
		return rulesString;
	}
	
	public Rule getRuleByName(String ruleName) {
		for (Rule rule : rules) {
			if(rule.getName().equals(ruleName))
				return rule;
		}
		return null;
	}

	public void setRulesString(String rulesString) {
		this.rulesString = rulesString;
	}
	
	public void setPassed(boolean status) {
		passedAllRules = status;
	}
	
	public boolean hasPassedRules() {
		return passedAllRules;
	}

	public boolean passes(boolean stopOnFirstFailure) throws ValidationException {
		// If this field had been checked before 
		if(hadBeenChecked)
			return true;
		/**
		 * Here we check if this field must be the same as another field.
		 * If this field has the same value as the reference field, so all
		 * rules passes the validation, otherwise all rules fail.
		 */
		hasToBeLikeAnotherField();
		
		for (Rule rule : rules) {
			if(!isRequired() && (value == null || value.isBlank()))
				continue;
		
			if(!rule.passes(value)) {
				failedRules.add(rule);
				if(stopOnFirstFailure)
					break;
			}
		}
		
		if(failedRules.size() > 0)
			passedAllRules = false;
		else
			passedAllRules = true;
		
		hadBeenChecked = true;
		
		return passedAllRules;
	}
	
	/**
	 * Here we check if this field must be the same as another field.
	 * If this field has the same value as the reference field, so all
	 * rules passes the validation, otherwise all rules fail.
	 */
	private void hasToBeLikeAnotherField() {
		if(sameField != null) {
			boolean same = false;
			if(value.equals(sameField.getValue()))
				same = true;
			
			for (Rule rule : rules) {
				if(same)
					rule.setMatcher(value->true);
				else
					rule.setMatcher(value->false);
			}
		}
	}

	public boolean hasRule(String ruleName) {
		for (Rule rule : rules)
			if(rule.getName().equals(ruleName))
				return true;
		
		return false;
	}
	
	public void addRule(Rule rule) {
		rules.add(rule);
	}
	
	public void deleteRule(Rule rule) {
		rules.remove(rule);
	}
	
	public ArrayList<Rule> getRules(){
		return rules;
	}
	
	public ArrayList<Rule> getFailedRules(){
		return failedRules;
	}
	
}
