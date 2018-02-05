import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONArray;
import org.json.JSONObject;

import java.security.SignatureException;
import java.util.Date;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

class Weather {

    private String TIANQI_DAILY_WEATHER_URL = "https://api.seniverse.com/v3/weather/daily.json";

    private String TIANQI_API_SECRET_KEY = "lzuouweqz5pj1y1v"; //

    private String TIANQI_API_USER_ID = "UB73E93120"; //

    /**
     * Generate HmacSHA1 signature with given data string and key
     * @param data
     * @param key
     * @return
     * @throws SignatureException
     */
    private String generateSignature(String data, String key) throws SignatureException {
        String result;
        try {
            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA1");
            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);
            // compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes("UTF-8"));
            result = new sun.misc.BASE64Encoder().encode(rawHmac);
        }
        catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
    }

    /**
     * Generate the URL to get diary weather
     * @param location
     * @param language
     * @param unit
     * @param start
     * @param days
     * @return
     */
    public String generateGetDiaryWeatherURL(
            String location,
            String language,
            String unit,
            String start,
            String days
    )  throws SignatureException, UnsupportedEncodingException {
        String timestamp = String.valueOf(new Date().getTime());
        String params = "ts=" + timestamp + "&ttl=180&uid=" + TIANQI_API_USER_ID;
        String signature = URLEncoder.encode(generateSignature(params, TIANQI_API_SECRET_KEY), "UTF-8");
        return TIANQI_DAILY_WEATHER_URL + "?" + params + "&sig=" + signature + "&location=" + location + "&language=" + language + "&unit=" + unit + "&start=" + start + "&days=" + days;
    }

    
    //DEMO
    public static void main(String args[]){
        Weather weather = new Weather();
        String url=new String();
        try {
        	//中文转拼音
        	String text="深圳";
       	 	PinyinTool tool=new PinyinTool();
       	 	String location_pinyin=tool.toPinYin(text, "", PinyinTool.Type.LOWERCASE);
        	
             url = weather.generateGetDiaryWeatherURL(
                    location_pinyin,
                    "zh-Hans",
                    "c",
                    "0",
                    "3"
            );
            
            System.out.println(url); 
             
            UrlReqUtil uru=new UrlReqUtil();
            boolean success=uru.get(url);
            if(success)
            {
            	JSONArray result=uru.getResult();
            	System.out.println(result);
            }
            
        } catch (Exception e) {
            System.out.println("Exception:" + e);
        }

    }
}