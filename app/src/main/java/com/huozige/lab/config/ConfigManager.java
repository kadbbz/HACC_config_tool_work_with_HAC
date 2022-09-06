package com.huozige.lab.config;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * 配置相关的帮助类
 */
public class ConfigManager {

    static final String PREFERENCE_NAME = "HACC";
    static final String PREFERENCE_KEY_ENTRY = "E"; // 页面入口的地址
    static final String PREFERENCE_KEY_SCAN_ACTION = "SA"; // 扫描广播的Action
    static final String PREFERENCE_KEY_SCAN_EXTRA = "SE"; // 扫描广播的Action

    private final Activity _context;

    public ConfigManager(Activity context){
        _context = context;
    }

    public  String GetEntry(){
        return GetStringValue(_context, PREFERENCE_KEY_ENTRY, R.string.default_config_url);
    }

    public  String GetScanAction(){
        return GetStringValue(_context, PREFERENCE_KEY_SCAN_ACTION, R.string.default_scan_action);
    }

    public  String GetScanExtra(){
        return GetStringValue(_context, PREFERENCE_KEY_SCAN_EXTRA, R.string.default_scan_extra);
    }

    public  void UpsertEntry(String value){
        UpsertStringValue(_context,PREFERENCE_KEY_ENTRY,value);
    }

    public  void UpsertScanAction(String value){
        UpsertStringValue(_context,PREFERENCE_KEY_SCAN_ACTION,value);
    }

    public  void UpsertScanExtra(String value){
        UpsertStringValue(_context,PREFERENCE_KEY_SCAN_EXTRA,value);
    }

    /**
     * 读取配置
     */
    public static String GetStringValue(Activity context, String key, int defaultValueStringId) {
        // 从配置库中读取启动地址
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        return sharedPref.getString(key, context.getString(defaultValueStringId));
}

    /**
     * 写入配置
     */
    public static void UpsertStringValue(Activity context, String key, String value){
        // 打开配置库
        SharedPreferences sharedPref = context.getSharedPreferences(
                PREFERENCE_NAME, Activity.MODE_PRIVATE);

        // 保存到配置库
        sharedPref.edit().putString(key, value).apply();
    }

}
