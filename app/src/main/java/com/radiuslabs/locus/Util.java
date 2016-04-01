package com.radiuslabs.locus;

import com.radiuslabs.locus.models.User;

public class Util {

    public static User user = null;

    public static boolean isStringEmpty(String str){
        return str == null || str.length() == 0;
    }

}
