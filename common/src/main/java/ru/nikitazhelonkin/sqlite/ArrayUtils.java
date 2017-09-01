package ru.nikitazhelonkin.sqlite;


/**
 * Created by nikita on 01.09.17.
 */

public class ArrayUtils {

    public static String join(String[] array, String delimiter) {
        if (array == null || array.length == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        boolean firstTime = true;
        for (Object token : array) {
            if (firstTime) {
                firstTime = false;
            } else {
                sb.append(delimiter);
            }
            sb.append(token);
        }
        return sb.toString();
    }

    public static String[] split(String string, String delimiter) {
        if (string == null || string.length() == 0) {
            return null;
        }
        return string.split(delimiter);
    }


}
