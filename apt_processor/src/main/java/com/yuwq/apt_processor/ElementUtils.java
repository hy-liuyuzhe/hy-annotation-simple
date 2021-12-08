package com.yuwq.apt_processor;

import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * @author liuyuzhe
 */
public class ElementUtils {

    public static String getPackageName(Elements elements, TypeElement typeElement){
        return elements.getPackageOf(typeElement).getQualifiedName().toString();
    }

    public static String getEnclosingClassName(TypeElement typeElement){
        return typeElement.getSimpleName().toString();
    }

}
