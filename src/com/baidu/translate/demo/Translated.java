package com.baidu.translate.demo;
import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.baidu.translate.demo.TransApi;

public class Translated{

    // 在平台申请的APP_ID 详见 http://api.fanyi.baidu.com/api/trans/product/desktop?req=developer
    private static final String APP_ID = "20171119000097280";
    private static final String SECURITY_KEY = "kz0kH4nhXnPB8Evgnh8e";

    public static String getResult(String src) throws UnsupportedEncodingException, JSONException {
        TransApi api = new TransApi(APP_ID, SECURITY_KEY);
        String result=api.getTransResult(src, "auto", "auto");
        JSONObject jsonResult=new JSONObject(result);
        JSONArray transArray=(JSONArray)jsonResult.get("trans_result");
        JSONObject jsonTrans=transArray.getJSONObject(0);
        return jsonTrans.get("dst").toString();
    }
}
