package com.hon.mylogger;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Frank Hon on 2019/6/29 12:59 PM.
 * E-mail: frank_hon@foxmail.com
 */
class Util {

    static String mapLogPriority(int priority){
        switch (priority){
            case 2:
                return "v";
            case 3:
                return "d";
            case 4:
                return "i";
            case 5:
                return "w";
            case 6:
                return "e";
            case 7:
                return "assert";
        }

        return "unknown";
    }

    static String getNowMDHMSTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM-dd HH:mm:ss", Locale.CHINA);
        return dateFormat.format(new Date());
    }

}
