package com.hon.apt_processor.util;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * Created by Frank_Hon on 1/10/2019.
 * E-mail: v-shhong@microsoft.com
 */
public class ElementUtils {
    //获取包名
    public static String getPackageName(Elements elements, TypeElement typeElement) {
        return elements.getPackageOf(typeElement).getQualifiedName().toString();
    }

    //获取顶层类类名
    public static String getEnclosingClassName(TypeElement typeElement) {
        return typeElement.getSimpleName().toString();
    }

    //获取静态内部类类名
    public static String getStaticClassName(TypeElement typeElement) {
        return getEnclosingClassName(typeElement) + "Holder";
    }
}
