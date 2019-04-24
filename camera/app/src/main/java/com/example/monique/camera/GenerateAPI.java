package com.example.monique.camera;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
/*
https://platform.fatsecret.com/api/Default.aspx?screen=rapiauth
creating authentication for api use according to companies design
 */
public class GenerateAPI {
    private String consumer_key = "f1f2b91b71294a4f890c4068cf043d44";
    private String private_key = "";
    private String http_method = "GET";
    private String request_url = "https://platform.fatsecret.com/rest/server.api";
    // if api uses any of these, be sure that it includes them in this order
    private String food_id = "";
    private String method = "";
    private String nonce = "";
    private String signature_method = "HMAC-SHA1";
    private String signature = "";
    private String timestamp = "";
    private String version = "1.0";

    public String generateTimestamp(){
        return Long.toString(System.currentTimeMillis() / 1000 );
    }

    public String generateAuthParameters() {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("oauth_consumer_key=%s&", this.consumer_key));
        sb.append(String.format("oauth_nonce=%s&", this.timestamp+"hmhm"));
        sb.append(String.format("oauth_timestamp=%s&", this.timestamp));
        sb.append(String.format("oauth_version=%s", version));
        return sb.toString();
    }
    public String generateAuthBaseString() {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s&", this.http_method));
        sb.append(String.format("%s&", encodeString(this.request_url)));
        sb.append(encodeString(generateAuthParameters()));

        return sb.toString();
    }

    public String encodeString(String str) {

        String url = "";
        try{
           url = URLEncoder.encode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e){
            Log.d("Encoding went wrong", e.toString());
        }
        return url;
    }

    public void generateSignature(){
        this.timestamp = generateTimestamp();

    }

}
