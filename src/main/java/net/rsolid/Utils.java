package net.rsolid;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static boolean isanameIllegal(String str){
        String PATTERN="^[HZSBTFJ]\\d{8}$";
        Pattern pattern=Pattern.compile(PATTERN);
        Matcher matcher=pattern.matcher(str);
        return matcher.matches();
    }
    public static boolean isInteger(String str){
        if (str==null||str.isEmpty()){
            return false;
        }
        try {
            Integer.parseInt(str);
            return true;
        }catch (NumberFormatException e){
            return false;
        }
    }
    public static boolean isDocNameIllegal(String str){
        String PATTERN= "^[A-Z]{2}\\d{3}$|^[A-Z]{3}\\d{2}$";
        Pattern pattern=Pattern.compile(PATTERN);
        Matcher matcher=pattern.matcher(str);
        return matcher.matches();
    }
    public static void  SexToChar(Map<String,Character> map){

    }
}
