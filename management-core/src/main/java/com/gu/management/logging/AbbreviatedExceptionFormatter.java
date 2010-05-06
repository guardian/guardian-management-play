/*
 * Copyright 2010 Guardian News and Media
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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
