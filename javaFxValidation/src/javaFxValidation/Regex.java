package javaFxValidation;

import java.util.regex.Pattern;

public class Regex {

	public static boolean matches(String value,String pattern) {
		if(pattern == null)
			return false;
		
		Pattern pat = Pattern.compile(pattern);
		
		return pat.matcher(value).matches();
	}
}
