package javaFxValidation.rules;

public class RuleMessage {

	private String fieldName;
	private String ruleName;
	private String message;
	
	public RuleMessage(String fieldName, String ruleName, String message) {
		this.fieldName = fieldName;
		this.ruleName = ruleName;
		this.message = message;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
