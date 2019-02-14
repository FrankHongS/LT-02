package com.hon.apt_annotation;

import java.lang.reflect.Method;

/**
 * Created by Frank_Hon on 1/10/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class MyButterKnife {

    @SuppressWarnings("unchecked")
    public static void bind(Object activity) {
        Class clazz = activity.getClass();
        try {
            Class bindViewClass = Class.forName(clazz.getName() + "ViewBinding");
            Method method = bindViewClass.getMethod("bind", activity.getClass());
            method.invoke(bindViewClass.newInstance(), activity);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
