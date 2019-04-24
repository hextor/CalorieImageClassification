package com.example.monique.camera;

import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/*
https://platform.fatsecret.com/api/Default.aspx?screen=rapiauth
creating authentication for api use according to companies design
 */
public class GenerateAPI {


    private String consumer_key = "f1f2b91b71294a4f890c4068cf043d44";
    private String private_key = "b5756c64465d41ea99aacd574afbdc95";
    private String http_method = "GET";
    private String request_url = "https://platform.fatsecret.com/rest/server.api";
    // if api uses any of these, be sure that it includes them in this order
    private String food_id = "";
    private String format = "json";
    private String method = ""; // should be food.search or food.get
    private String nonce = ""; // unique string to be passed
    private String searchExpression = "";
    private String signature_method = "HMAC-SHA1";
    private String signature = "";
    private String timestamp = "";
    private String version = "1.0";

    public GenerateAPI() { }

    public String encodeString(String str) {

        String url = "";
        try{
            url = Uri.encode(str, "UTF-8").trim();
//            url = URLEncoder.encode(str, "UTF-8");
        }
        catch (Exception e){
            Log.d("Encoding went wrong", e.toString());
        }
        return url;
    }

    public String generateTimestamp(){
        return Long.toString(System.currentTimeMillis() / 1000 );
    }

    public String generateAuthParameters() {

        StringBuilder sb = new StringBuilder();
        sb.append(String.format("format=%s&", this.format));
        sb.append(String.format("method=%s&", this.method));
        sb.append(String.format("oauth_consumer_key=%s&", this.consumer_key));
        sb.append(String.format("oauth_nonce=%s&", this.timestamp+"okay"));
        sb.append("oauth_signature_method=HMAC-SHA1&");
        sb.append(String.format("oauth_timestamp=%s&", this.timestamp));
        sb.append(String.format("oauth_version=%s&", version));
        sb.append(String.format("search_expression=%s", this.searchExpression));
        return sb.toString();
    }

    public String generateSignature(){
        // set up key with private_key + `&`
        String key = String.format("%s&", this.private_key);
        // set up base key <HTTP Method>&<Request URL>&<Normalized Parameters>
        String base = String.format("%s&%s&%s", http_method, encodeString(request_url), encodeString(generateAuthParameters()));

        Mac mac;
        byte[] digest = null;
        try{
            mac = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
            mac.init(secret);
            digest = mac.doFinal(base.getBytes());
        } catch (Exception e){
            Log.d("error on key algo: ", e.toString());
        }

//        byte[] result = Base64.encode(digest, 0);
        String result = Uri.encode(Base64.encodeToString(digest, 0).trim());
        Log.d("new encoded base signature", result);
        return result;
    }

    // now we construct a url for searching for food item by search term
    public String searchFoodItem(String searchTerm){
        this.timestamp = generateTimestamp();
        this.searchExpression = searchTerm;
        this.method = "foods.search";
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s?", this.request_url));
        sb.append(String.format("format=%s&", this.format));
        sb.append(String.format("method=%s&", this.method));
        sb.append(String.format("oauth_consumer_key=%s&", this.consumer_key));
        sb.append(String.format("oauth_nonce=%s&", this.timestamp + "okay"));
        sb.append(String.format("oauth_signature=%s&", this.generateSignature()));
        sb.append(String.format("oauth_signature_method=HMAC-SHA1&"));
        sb.append(String.format("oauth_timestamp=%s&", this.timestamp));
        sb.append(String.format("oauth_version=%s&", this.version));
        sb.append(String.format("search_expression=%s", this.searchExpression));
        Log.d("URL being sent over", sb.toString());
        return sb.toString();
    }

}
