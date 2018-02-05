import org.json.JSONArray;
import org.json.JSONObject;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 实现GET请求
 * mail:1147649695@qq.com
 */
public class UrlReqUtil {
	private JSONArray result;
	
	public JSONArray getResult(){ return result; }
	
    public boolean get(String url)
    {
        HttpURLConnection http = null;
        InputStream is = null;
        try {
            URL urlGet = new URL(url);
            http = (HttpURLConnection) urlGet.openConnection();

            http.setRequestMethod("GET");
            http.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
            http.setDoOutput(true);
            http.setDoInput(true);
            System.setProperty("sun.net.client.defaultConnectTimeout", "30000");
            System.setProperty("sun.net.client.defaultReadTimeout", "30000");

            http.connect();

            is =http.getInputStream();
            int size =is.available();
            byte[] jsonBytes =new byte[size];
            is.read(jsonBytes);
            String message=new String(jsonBytes,"UTF-8");
            JSONObject return_json=new JSONObject(message);
            if(return_json==null) return false;
            else 
            {
            	this.result=return_json.getJSONArray("results").getJSONObject(0).getJSONArray("daily");

            	return true;
            }
        } catch (Exception e) {
            return false;
        }finally {
            if(null != http) http.disconnect();
            try {
                if (null != is) is.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }

    }

    
    //DEMO
    public static void main(String[] args)throws Exception
    {
    	UrlReqUtil uru=new UrlReqUtil();
    	
    	String url=new String("https://api.seniverse.com/v3/weather/daily.json?ts=1517750147820&ttl=180&uid=UB73E93120&sig=Be3aTVQjnfRGfQphrcp%2BTHUtC0o%3D&location=shenzhen&language=zh-Hans&unit=c&start=1&days=1");
 
    	boolean success=uru.get(url);
    	if(success)
    	{
    		JSONArray result=uru.getResult();
    		System.out.println(result.getJSONObject(0).getString("text_day"));
    	}
    }
}