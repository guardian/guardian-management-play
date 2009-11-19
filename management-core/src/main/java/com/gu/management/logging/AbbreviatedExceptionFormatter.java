package com.gu.management.logging;

public class AbbreviatedExceptionFormatter {

    private AbbreviatedExceptionFormatter() {
	}

	public static String format(Throwable e) {
		return formatWithSeparator(e, "\n\t");
	}

	public static String formatHtml(Throwable e) {
		return formatWithSeparator(e, "<br />");
	}

	private static String formatWithSeparator(Throwable e, String separator) {
		StringBuffer errorMessage = new StringBuffer();

		errorMessage.append("\n Caused by: ");

		Throwable currentException = e;

		while (currentException != null) {
			errorMessage.append(separator);
			errorMessage.append(currentException.getClass().getName());
			errorMessage.append(": ");
			errorMessage.append(currentException.getMessage());
			currentException = currentException.getCause();
		}

		return errorMessage.toString();
	}
}
