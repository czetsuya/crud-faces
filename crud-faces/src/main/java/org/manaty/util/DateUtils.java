/*
 * (C) Copyright 2009-2013 Manaty SARL (http://manaty.net/) and contributors.
 *
 * Licensed under the GNU Public Licence, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/gpl-2.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.manaty.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	public static boolean verifyDateWithPattern(String date, String pattern) {
		System.out.println("verifyDateWithPattern date=" + date + ",pattern="
				+ pattern);
		if (date == null)
			return false;

		// set the format to use as a constructor argument
		SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);

		if (date.trim().length() != dateFormat.toPattern().length())
			return false;

		dateFormat.setLenient(false);

		try {
			// parse the inDate parameter
			dateFormat.parse(date.trim());
		} catch (ParseException pe) {
			return false;
		}
		return true;
	}

	public static Date addMonthsToDate(Date date, Integer months) {
		Date result = null;

		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, months);
			result = calendar.getTime();
		}

		return result;
	}

	public static Date addYearsToDate(Date date, Integer years) {
		Date result = null;

		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.YEAR, years);
			result = calendar.getTime();
		}

		return result;
	}

	public static String formatDateWithPattern(Date value, String pattern) {
		String result = null;
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		try {
			result = sdf.format(value);
		} catch (Exception e) {
			result = "";
		}

		return result;
	}

	public static Date parseDateWithPattern(Date value, String pattern) {
		String dateValue = formatDateWithPattern(value, pattern);
		return parseDateWithPattern(dateValue, pattern);
	}

	public static Date parseDateWithPattern(String dateValue, String pattern) {
		Date result = null;

		SimpleDateFormat sdf = new SimpleDateFormat(pattern);

		try {
			result = sdf.parse(dateValue);
		} catch (Exception e) {
			result = new Date(1);
		}

		return result;
	}

	public static Date addDaysToDate(Date date, Integer days) {
		Date result = null;

		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DATE, days);
			result = calendar.getTime();
		}

		return result;
	}

	public static Date setDateToStartOfDay(Date date) {
		Date result = null;

		if (date != null) {
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DATE);
			calendar.set(year, month, day, 0, 0, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			result = calendar.getTime();
		}

		return result;
	}
	
	public static int daysBetween(Date d1, Date d2){
		 return (int)( (d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
    }

}
