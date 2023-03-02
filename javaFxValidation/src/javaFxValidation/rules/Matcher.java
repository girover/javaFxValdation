package javaFxValidation.rules;

@FunctionalInterface
public interface Matcher {

	public boolean matches(String fieldValue);
}
