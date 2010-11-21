package de.bokelberg.flashbuilder.aca.utils;


public class StringUtil {

	public static String removeOptionalQuotes(String value) {
		return StringUtil.isQuotedString(value) ? StringUtil.removeQuotes(value) : value;
	}

	public static String removeQuotes(String value) {
		return value.substring(0, value.length() - 1);
	}

	public static boolean isQuotedString(String value) {
		return value.startsWith("\"") && value.endsWith("\"");
	}

	public static String addQuotesIfStringContainsBlanks( String value )
	{
		return value.contains(" ") ? "\"" + value + "\"" : value;
	}
	
	public static String substitute( String pattern, Object[] values )
	{
		for( int i = 0; i < values.length; i++ )
		{
			pattern = pattern.replace("{" + i + "}", values[i] + "");
		}
		return pattern;
	}

}
