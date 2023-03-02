package javaFxValidation.rules;

import java.util.ArrayList;

public class RuleMessagesBag extends ArrayList<RuleMessage>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 234176871368401798L;

	/**
	 * Determine if the bag has a message exists for a given field
	 * @param fieldName
	 * @return
	 */
	public boolean contains(String fieldName) {
		for (RuleMessage ruleMessage : this)
			if(ruleMessage.getRuleName().equals(fieldName))
				return true;
			
		return false;
	}
	
	/**
	 * Determine if message exists for a given field and rule
	 * @param fieldName String
	 * @param ruleName String
	 * @return
	 */
	public boolean contains(String fieldName, String ruleName) {
		for (RuleMessage ruleMessage : this)
			if(ruleMessage.getRuleName().equals(ruleName) && ruleMessage.getFieldName().equals(fieldName))
				return true;
		
		return false;
	}
	
	/**
	 * Determine if message exists for a given RuleMessage Object
	 * @param fieldName RuleMessage
	 * @return boolean
	 */
	public boolean contains(RuleMessage ruleMessage) {
		for (RuleMessage message : this)
			if(message.getRuleName().equals(ruleMessage.getRuleName()) && message.getFieldName().equals(ruleMessage.getFieldName()))
				return true;
		
		return false;
	}
	
	/**
	 * Get a messages for a given field
	 * @param fieldName
	 * @return ArrayList<RuleMesage>
	 */
	public ArrayList<RuleMessage> get(String fieldName){
		ArrayList<RuleMessage> messages = new ArrayList<>();
		
		for (RuleMessage ruleMessage : this)
			if(ruleMessage.getFieldName().equals(fieldName)) {
				messages.add(ruleMessage);
			}
		return messages;
	}
	
	/**
	 * Get a messages for a given field
	 * @param fieldName
	 * @return ArrayList<RuleMesage>
	 */
	public RuleMessage get(String fieldName, String ruleName){
		
		for (RuleMessage ruleMessage : this)
			if(ruleMessage.getFieldName().equals(fieldName) && ruleMessage.getRuleName().equals(ruleName))
				return ruleMessage;

		return null;
	}
}
