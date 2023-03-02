package javaFxValidation;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public abstract class Str {

	/**
	 * Determines if a string is not empty
	 * @param string
	 * @return
	 */
	public static boolean isEmpty(String string) {
		return string == null || string.isBlank() ? true : false;
	}
	
	public static boolean isInteger(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Determines if a string is Double.
	 * @param string
	 * @return
	 */
	public static boolean isDouble(String string) {
		try {
			Double.parseDouble(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Determines if a string is a valid email address.
	 * @param string
	 * @return
	 */
	public static boolean isEmail(String string) {

		return Regex.matches(string, "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$");
	}

	/**
	 * Determines if a string only contains letters.
	 * @param string
	 * @return
	 */
	public static boolean isAlpha(String string) {

		return Regex.matches(string, "^[\\pL\\pM]+$");
	}

	/**
	 * Determine if a string only contains letters and numbers.
	 * @param string
	 * @return
	 */
	public static boolean isAlphaNumeric(String string) {

		return Regex.matches(string, "^[\\pL\\pM\\pN]+$");
	}

	/**
	 * determine if a string consists of letters, numbers, dashes or underscores
	 * @param string
	 * @return
	 */
	public static boolean isAlphaDash(String string) {

		return Regex.matches(string, "^[\\pL\\pM\\pN_-]+$");
	}

	/**
	 * Determine if a string is a number.
	 * @param string
	 * @return
	 */
	public static boolean isNumeric(String string) {
		
		return Regex.matches(string, "^[\\pN]+$");
	}
	
	/**
	 * Determine if a string is a boolean.
	 * @param string
	 * @return
	 */
	public static boolean isBoolean(String string) {
		return string == "true" || string == "false" ? true : false;
	}
	
	/**
	 * Determine if a string is a date of form dd/mm/yyyy.
	 * @param string
	 * @return boolean
	 */
	public static boolean isDate(String string) {
		String dateFormater = "dd/MM/yyyy";
		return isDate(string, dateFormater);
	}
	
	public static boolean isDate(String date, String format) {
		String dateFormater = format;
		DateFormat sdf = new SimpleDateFormat(dateFormater);
		sdf.setLenient(false);
		try {
			sdf.parse(date);
			return true;
		} catch (ParseException e) {
			return false;
		}
	}
}
