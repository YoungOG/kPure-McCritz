package com.mccritz.kpure.utils;

import java.util.regex.Pattern;

public class IPUtils {

    public static Pattern IP_PATTERN = Pattern.compile("([0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3}.[0-9]{1,3})");

    public static boolean isValidIP(String ip) {
	return IP_PATTERN.matcher(ip).matches();
    }
}