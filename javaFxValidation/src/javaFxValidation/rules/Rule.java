package javaFxValidation.rules;

import javaFxValidation.ValidationException;

public abstract class Rule {

	protected String name;       // required
	protected String message;    // field is required
	protected boolean passed = false;  // Determine if data passed this rule.
	
	protected Matcher matcher;
	
	public Rule(String name) {
		this.name = name;
	}

	/**
	 * Set a matcher for this rule
	 * This matcher is responsible for matching the value for a given rule
	 * @param matcher
	 */
	public void setMatcher(Matcher matcher) {
		this.matcher = matcher;
	}
	
	public Matcher getMatcher() {
		return matcher;
	}
	
	public boolean passes(String fieldValue) throws ValidationException {
		passed = matcher.matches(fieldValue);
		return isPassed();
	}
	
	public boolean isPassed() {
		return passed;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
