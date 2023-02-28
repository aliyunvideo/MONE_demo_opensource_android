package com.aliyun.player.alivcplayerexpand.util;

import com.cicada.player.utils.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @Date: 2017/3/3.
 * @Description:
 */

public class JsonUtil {

    private static final int    INT_EMPTY_VALUE = 0;
    private static final String STR_EMPTY_VALUE = "";

    public static int getInt(JSONObject jsonObject, String... name) {
        if (jsonObject == null) {
            return INT_EMPTY_VALUE;
        }

        for (String oneName : name) {
            try {
                return jsonObject.getInt(oneName);
            } catch (JSONException e) {
                continue;
            }
        }

        for (String oneName : name) {
            Logger.w("JsonUtil", "No json int value for " + oneName);
        }

        return INT_EMPTY_VALUE;
    }


    public static String getString(JSONObject jsonObject, String... name) {
        if (jsonObject == null) {
            return STR_EMPTY_VALUE;
        }

        for (String oneName : name) {
            try {
                return jsonObject.getString(oneName);
            } catch (JSONException e) {
                continue;
            }
        }

        for (String oneName : name) {
            Logger.w("JsonUtil", "No json string value for " + oneName);
        }

        return STR_EMPTY_VALUE;
    }

    public static JSONObject getJSONObject(JSONObject jsonObject, String... name) {
        if (jsonObject == null) {
            return null;
        }

        for (String oneName : name) {
            try {
                return jsonObject.getJSONObject(oneName);
            } catch (JSONException e) {
                continue;
            }
        }

        for (String oneName : name) {
            Logger.w("JsonUtil", "No json object value for " + oneName);
        }

        return null;
    }

    public static double getDouble(JSONObject jsonObject, String... name) {
        if (jsonObject == null) {
            return 0;
        }

        for (String oneName : name) {
            try {
                return jsonObject.getDouble(oneName);
            } catch (JSONException e) {
                continue;
            }
        }

        for (String oneName : name) {
            Logger.w("JsonUtil", "No json double value for " + oneName);
        }

        return 0;
    }

    public static long getLong(JSONObject jsonObject, String... name) {
        if (jsonObject == null) {
            return 0;
        }

        for (String oneName : name) {
            try {
                return jsonObject.getLong(oneName);
            } catch (JSONException e) {
                continue;
            }
        }

        for (String oneName : name) {
            Logger.w("JsonUtil", "No json long value for " + oneName);
        }

        return 0;
    }

    public static JSONArray getArray(JSONObject jsonObject, String... name) {
        if (jsonObject == null) {
            return null;
        }

        for (String oneName : name) {
            try {
                return jsonObject.getJSONArray(oneName);
            } catch (JSONException e) {
                continue;
            }
        }

        for (String oneName : name) {
            Logger.w("JsonUtil", "No json long value for " + oneName);
        }

        return null;
    }


    public static JSONObject getJSONObjectAt(JSONArray jsonArray, int index) {
        if (jsonArray == null) {
            return null;
        }

        try {
            return jsonArray.getJSONObject(index);
        } catch (JSONException e) {

        }

        return null;
    }

    public static boolean getBoolean(JSONObject jsonObject, String... name) {
        if (jsonObject == null) {
            return false;
        }

        for (String oneName : name) {
            try {
                return jsonObject.getBoolean(oneName);
            } catch (JSONException e) {
                continue;
            }
        }

        for (String oneName : name) {
            Logger.w("JsonUtil", "No json boolean value for " + oneName);
        }

        return false;
    }
}
