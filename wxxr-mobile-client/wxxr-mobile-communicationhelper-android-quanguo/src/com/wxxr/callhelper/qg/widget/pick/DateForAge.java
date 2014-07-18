package com.wxxr.callhelper.qg.widget.pick;

import java.util.ArrayList;
import java.util.List;

public class DateForAge {
	// from 1990 to 2099
	public static List<String> getYears() {
		ArrayList<String> years = new ArrayList<String>();
		int start = 1900;
		while (start <= 2099) {
			years.add(start + "年");
			start++;
		}
		return years;

	}
	public static List<String> getMonths() {
		ArrayList<String> months = new ArrayList<String>();
		int start = 1;
		while (start <= 12) {
			months.add(start + "月");
			start++;
		}
		return months;
	}
	public static List<String> getDay() {
		ArrayList<String> dates = new ArrayList<String>();
		int start = 1;
		while (start <= 31) {
			dates.add(start + "日");
			start++;
		}
		return dates;
	}
}
