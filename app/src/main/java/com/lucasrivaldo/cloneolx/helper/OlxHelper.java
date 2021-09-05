package com.lucasrivaldo.cloneolx.helper;

public class OlxHelper {

    public static boolean detailsAlertLoopHolder = false;

    public static String formatPhoneNumber(String input){
        return input.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{3})",
                             "($1) $2-$3-$4");
    }
}
