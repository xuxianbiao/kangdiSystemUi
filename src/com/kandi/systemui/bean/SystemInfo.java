package com.kandi.systemui.bean;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SystemInfo {
	private Method methodGetProperty;
	private static SystemInfo instance = new SystemInfo();

	public static SystemInfo getInstance() {
		return instance;
	}

	private SystemInfo() {
		try {
			Class classSystemProperties = Class
					.forName("android.os.SystemProperties");
			methodGetProperty = classSystemProperties.getMethod("get",
					String.class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	public String getProperty(String property) {
		if (methodGetProperty == null)
			return null;
		try {
			return (String) methodGetProperty.invoke(null, property);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
}