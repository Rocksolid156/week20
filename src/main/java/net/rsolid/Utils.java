package net.rsolid;

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
    public static <T extends Comparable<? super T>>int binarySearch(T[] source, T x, int begin,int end){
//        int end=source.length-1,mid=end;
        if (begin<=end){
            int mid=(begin+end)/2;
            if (x.compareTo(source[mid])==0){
                return mid;
            }
            if (x.compareTo(source[mid])<0){    //若x对象小
                return binarySearch(source,x,begin,mid-1);  //查找范围缩小为前半段
            }else {
                return binarySearch(source,x,mid+1,end);    //查找范围缩小为后半段
            }
        }
        return -1;  //NOT Found
    }
    public static <T extends Comparable<? super T>> int binarySearch(T[] source, T x) {
        return binarySearch(source, x, 0, source.length - 1);
    }   //二分法查找
}
