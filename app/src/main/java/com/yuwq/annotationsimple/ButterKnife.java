package com.yuwq.annotationsimple;

import android.app.Activity;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * @author liuyuzhe
 */
public class ButterKnife {

    public static void bind(Activity activity) {
        Class<? extends Activity> clazz = activity.getClass();
        try {
            Class<?> bindViewClass = Class.forName(clazz.getName() + "ViewBinding");
            Method method = bindViewClass.getMethod("bind", activity.getClass());
            method.invoke(bindViewClass.newInstance(), activity);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("liuyuzhe", "ButterKnife.bind: "+e.getMessage());
        }
    }
}
