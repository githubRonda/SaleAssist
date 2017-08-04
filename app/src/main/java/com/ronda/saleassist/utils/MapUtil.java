package com.ronda.saleassist.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapUtil {
    /**
     * javaBean 转 Map
     * 
     * @param object
     *            需要转换的javabean
     * @return 转换结果map
     * @throws Exception
     */
    public static Map<String, Object> beanToMap(Object object) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>();

        Class cls = object.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), field.get(object));
        }
        return map;
    }
    
    
    /**
     * javaBean 转 Map. map集合的值都为String类型
     * 
     * @param object
     * @return
     * @throws Exception
     */
    public static Map<String, String> beanToStringMap(Object object) throws Exception {
        Map<String, String> map = new HashMap<String, String>();

        Class cls = object.getClass();
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            map.put(field.getName(), (String)field.get(object));
        }
        return map;
    }

    /**
     *
     * @param map
     *            需要转换的map
     * @param cls
     *            目标javaBean的类对象
     * @return 目标类object
     * @throws Exception
     */
    public static Object mapToBean(Map<String, Object> map, Class cls) throws Exception {
        Object object = cls.newInstance();
        for (String key : map.keySet()) {
            Field temFiels = cls.getDeclaredField(key);
            temFiels.setAccessible(true);
            temFiels.set(object, map.get(key));
        }
        return object;
    }
}
