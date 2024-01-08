package com.alivc.live.commonbiz.seidelay.data;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author keria
 * @date 2023/12/5
 * @brief
 */
public class SEIDelayProtocol {

    private static final String SEI_DELAY = "sei_delay";

    private static final String TIMESTAMP = "tm";
    private static final String SRC = "src";

    public String src = null;
    public long tm = -1L;

    public SEIDelayProtocol(String src, long tm) {
        this.src = src;
        this.tm = tm;
    }

    public SEIDelayProtocol(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            String seiDelay = jsonObject.getString(SEI_DELAY);

            JSONObject jsonObject2 = new JSONObject(seiDelay);
            this.src = jsonObject2.getString(SRC);
            this.tm = jsonObject2.getLong(TIMESTAMP);
        } catch (JSONException e) {
        }
    }

    private String generate() {
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put(SRC, this.src);
            jsonObject1.put(TIMESTAMP, this.tm);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject jsonObject2 = new JSONObject();
        try {
            jsonObject2.put(SEI_DELAY, jsonObject1.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject2.toString();
    }

    @Override
    public String toString() {
        return generate();
    }

}
