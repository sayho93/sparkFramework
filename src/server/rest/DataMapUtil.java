package server.rest;

import server.comm.DataMap;

/**
 * @author 함의진
 * @version 1.0.0
 * DataMap 검증을 위한 Validation 클래스
 */
public class DataMapUtil {

    private static final String EMPTY_STRING = "";
    private static final String MASK_STRING = "**********";

    /**
     * String 가변 파라미터에 대해 Null 여부를 반환한다.
     * @param map 검증 대상 맵
     * @param args 가변 파라미터 - String 기반 키
     * @return
     */
    public static boolean isValid(DataMap map, String... args){
        for(String arg : args) if(map.get(arg) == null) return false;
        return true;
    }

    public static void mask(DataMap map, String key){
        map.put(key, MASK_STRING);
    }

    public static boolean isSatisfied(DataMap map, String... args){
        for(String arg : args)
            if(map.get(arg) == null){
                return false;
            }else{
                if(map.getString(arg).equals(EMPTY_STRING)) return false;
            }
        return true;
    }

    public static boolean isVoid(String str){
        return str == null || str.equals("");
    }

}
