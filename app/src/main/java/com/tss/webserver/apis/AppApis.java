/*
 * The MIT License
 *
 * Copyright 2018 Sonu Auti http://sonuauti.com twitter @SonuAuti
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.tss.webserver.apis;

import android.content.Intent;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tss.webserver.configulations.TinyWebServer;
import com.tss.webserver.configulations.TssSharedPreferences;
import com.tss.webserver.utils.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 *
 * @author cis
 */
public class AppApis {

    private static String KEY_SETUP_INTERNAL_SECRET_KEY = "internal_secret_key";
    private static String KEY_SETUP_MERCHANT_DATA = "merchant_data";
    private static String KEY_SETUP_MERCHANT_ENV = "merchant_env";
    private static String KEY_SETUP_KIOSK_ID = "kiosk_id";


    public AppApis(){
    }
    
    public String helloworld(HashMap qparms){
        System.out.println("test : "+qparms.get("test"));
        //demo of simple html webpage from controller method 
        TinyWebServer.CONTENT_TYPE="text/html";
        return "<html><head><title>Simple HTML and Javascript Demo</title>\n" +
                "  <script>\n" +
                "  \n" +
                "</script>\n" +
                "  \n" +
                "  </head><body style=\"text-align:center;margin-top: 5%;\" cz-shortcut-listen=\"true\" class=\"\">\n" +
                "    <h3>Say Hello !</h3>\n" +
                "<div style=\"text-align:center;margin-left: 29%;\">\n" +
                "<div id=\"c1\" style=\"width: 100px;height: 100px;color: gray;background: gray;border-radius: 50%;float: left;\"></div>\n" +
                "<div id=\"c2\" style=\"width: 100px;height: 100px;color: gray;background: yellow;border-radius: 50%;float: left;\"></div>\n" +
                "<div id=\"c3\" style=\"width: 100px;height: 100px;color: gray;background: skyblue;border-radius: 50%;float: left;\"></div>\n" +
                "<div id=\"c4\" style=\"width: 100px;height: 100px;color: gray;background: yellowgreen;border-radius: 50%;float: left;\"></div>\n" +
                "<div id=\"c5\" style=\"width: 100px;height: 100px;color: gray;background: red;border-radius: 50%;position: ;position: ;float: left;\" class=\"\"></div></div>\n" +
                "  </body></html>";
    }

    public String transaction(HashMap qparms) {
        String x = Util.getUUID();
        return "";
    }

    public String setup(HashMap qparms) {
        JSONObject response = new JSONObject();
        if (qparms.containsKey("_POST")) {
            String data = (String) qparms.get("_POST");
            HashMap<String,String> map = new Gson().fromJson(data, new TypeToken<HashMap<String, String>>(){}.getType());
            if (map.containsKey(KEY_SETUP_INTERNAL_SECRET_KEY) &&
                    map.containsKey(KEY_SETUP_MERCHANT_DATA) &&
                    map.containsKey(KEY_SETUP_MERCHANT_ENV) &&
                    map.containsKey(KEY_SETUP_KIOSK_ID)) {
                String secretKey = map.get(KEY_SETUP_INTERNAL_SECRET_KEY);
                String merchantData = map.get(KEY_SETUP_MERCHANT_DATA);
                String merchantEnv = map.get(KEY_SETUP_MERCHANT_ENV);
                String kioskId = map.get(KEY_SETUP_KIOSK_ID);
                SharedPreferences.Editor editor = TssSharedPreferences.getSharedpreferences().edit();
                editor.putString(KEY_SETUP_INTERNAL_SECRET_KEY, secretKey);
                editor.putString(KEY_SETUP_MERCHANT_DATA, merchantData);
                editor.putString(KEY_SETUP_MERCHANT_ENV, merchantEnv);
                editor.putString(KEY_SETUP_KIOSK_ID, kioskId);
                editor.apply();

                try {
                    response.putOpt("terminal_id", "xxx");
                    response.putOpt("sign", "yyy");
                    response.putOpt("status", "success");
                    response.putOpt("msg", "setup is success");
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "{'status':'fail', 'msg':'setup is failed'}";
                }
            }
        } else {
            try {
                response.putOpt("status", "fail");
                response.putOpt("msg", "parameter is missing");
            } catch (JSONException e) {
                e.printStackTrace();
                return "{'status':'fail', 'msg':'setup is failed'}";
            }
        }
        return  response.toString();
    }
    
    public String simplejson(HashMap qparms){
        //simple json output demo from controller method
        String json = "{\"name\":\"sonu\",\"age\":29}";
        return json.toString();
    }
    
    public String simplegetparm(HashMap qparms){
        /*
        qparms is hashmap of get and post parameter
        
        simply use qparms.get(key) to get parameter value
        user _POST as key for post data
        e.g to get post data use qparms.get("_POST"), return will be post method 
        data
        */
        
        System.out.println("output in simplehelloworld "+qparms);
        String p="";
        if(qparms!=null){
            p=qparms.get("age")+"";
        }
        String json = "{\"name\":\"sonu\",\"age\":"+p+",\"isp\":yes}";
        return json.toString();
    }
    
    
    //implement web callback here and access them using method name
}
