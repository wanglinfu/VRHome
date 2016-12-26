/**
 *
 */
package com.vrseen.vrstore.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

/**
 * @author jiangs
 * @ClassName SharedPreferencesUtils
 * @QQ 826565702
 * @date 2014-3-25上午9:47:52
 */
public class SharedPreferencesUtils {
    /**
     * 文件名
     */
    private static final String DEFAULT_FILE_NAME = "vrseen";
    /**
     * 缓存文件名
     */
    private static final String CACHE_FILE_NAME = "vrseen-cache";


    /**
     * 保存数据
     *
     * @param context
     * @param key
     * @param object
     */
    public static void setParam(Context context, String key, Object object) {
        setValue(context, DEFAULT_FILE_NAME, key, object, true);
    }

    public static void setParamByCommit(Context context, String key, Object object) {
        setValue(context, DEFAULT_FILE_NAME, key, object, false);
    }

    private static void setValue(Context context, String fileName, String key, Object object, boolean async) {
        if (context == null || TextUtils.isEmpty(key) || object == null) {
            return;
        }
        String type = object.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        if ("String".equals(type)) {
            editor.putString(key, (String) object);
        } else if ("Integer".equals(type)) {
            editor.putInt(key, (Integer) object);
        } else if ("Boolean".equals(type)) {
            editor.putBoolean(key, (Boolean) object);
        } else if ("Float".equals(type)) {
            editor.putFloat(key, (Float) object);
        } else if ("Long".equals(type)) {
            editor.putLong(key, (Long) object);
        } else if ("Double".equals(type)) {
            editor.putFloat(key, ((Double) object).floatValue());
        }
        if (async) {
            editor.apply();
        } else {
            editor.commit();
        }
    }


    /**
     * 保存缓存的数据
     *
     * @param context
     * @param key
     * @param object
     */
    public static void setCacheParam(Context context, String key, Object object) {
        setValue(context, CACHE_FILE_NAME, key, object, true);
    }


    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject 不能为null
     * @return
     */
    private static Object getValue(Context context, String fileName, String key,
                                   Object defaultObject) {
        if (context == null || TextUtils.isEmpty(key)) {
            return null;
        }
        String type = defaultObject.getClass().getSimpleName();
        SharedPreferences sp = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);


        if ("String".equals(type)) {
            return sp.getString(key, (String) defaultObject);
        } else if ("Integer".equals(type)) {
            return sp.getInt(key, (Integer) defaultObject);
        } else if ("Boolean".equals(type)) {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if ("Float".equals(type)) {
            return sp.getFloat(key, (Float) defaultObject);
        } else if ("Long".equals(type)) {
            return sp.getLong(key, (Long) defaultObject);
        } else if ("Double".equals(type)) {
            return Double.valueOf(sp.getFloat(key,
                    ((Double) defaultObject).floatValue()));
        }

        return null;
    }

    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject 不能为null
     * @return
     */
    public static Object getParam(Context context, String key,
                                  Object defaultObject) {
        return getValue(context, DEFAULT_FILE_NAME, key, defaultObject);
    }

    /**
     * 得到保存缓存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     *
     * @param context
     * @param key
     * @param defaultObject 不能为null
     * @return
     */
    public static Object getCacheParam(Context context, String key,
                                       Object defaultObject) {
        return getValue(context, CACHE_FILE_NAME, key, defaultObject);
    }

    /**
     * 切换boolean的值
     *
     * @param context
     * @param key
     * @param defaultValue 未存储时使用的默认值，将对此值取反后存入
     * @return 新的值
     */
    public static boolean toggleBooleanValue(Context context, String key,
                                             Boolean defaultValue) {
        boolean original = (Boolean) getParam(context, key, defaultValue);
        boolean newconfig = !original;
        setParam(context, key, newconfig);
        return newconfig;
    }


}
