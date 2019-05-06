package com.example.monique.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    private ImageView mimageView;
    private static final int REQUEST_IMAGE_CAPTURE= 101;
    private Runnable recognizer;
    private Classifier classifier;
    private String mainFoodName = "";
    private String mainFoodID = "";
    private String allNutrition = "";

    private Map<String, String> foodVariables = new HashMap<String, String>() {
        {
            put("measurement_description", "Measurement Description: ");
            put("calories", "Calories: ");
            put("carbohydrate", "Carbs: ");
            put("protein", "Protein: ");
            put("fat", "Fat: ");
            put("saturated_fat", "Saturated Fat: ");
            put("polyunsaturated_fat", "Polyunsaturated Fat: ");
            put("monounsaturated_fat", "Monounsaturated Fat: ");
            put("trans_fat", "Trans Fat: ");
            put("cholesterol", "Cholesterol: ");
            put("potassium", "Potassium: ");
            put("sodium", "Sodium: ");
            put("fiber", "Fiber: ");
            put("sugar", "Sugar: ");
            put("vitamin_a", "Vitamin A: ");
            put("vitamin_c", "Vitamin C: ");
            put("calcium", "Calcium: ");
            put("iron", "Iron: ");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mimageView=findViewById(R.id.imageView);
        try{
            classifier = new ClassifierQuantizedMobileNet(this);
        }
        catch (Exception e){
            Log.d("Error on classifier initialization", e.toString());
        }

    }

    public void capture(View view) {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (imageTakeIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        {
            // After image is taken, it will fall into this statement if successful
            if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                final Bitmap imageBitmap = (Bitmap) extras.get("data");
                mimageView.setImageBitmap(imageBitmap);

                final List<Classifier.Recognition> results = classifier.recognizeImage(imageBitmap);
                final TextView displayResults = findViewById(R.id.calorieTextView);
                final TextView foodIDView = findViewById(R.id.foodIdView);
                final TextView saveFoodID = findViewById(R.id.foodID);
                // run in debug mode and hover over this line to see data details.
                Log.d("Results for image", results.toString());  // GET THE FIRST KEYWORD -- results.get(0).getTitle()

                GenerateAPI api = new GenerateAPI();
                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);
                // Request a json response from the provided URL.
//                String url = api.generateSignature();

                String url;
//                int count = 0;
//                do{
                    url = api.searchFoodItem(results.get(0).getTitle());
//                    count++;
//                }while(url.contains("%2B") && count < 3);
                Log.d("API URL", url);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        // if all good you can set the values to a variable or just display the results directly.
                        Log.d("Response from FatSecret", response.toString());
                                            try{
                                                JSONObject res = new JSONObject(response.getJSONObject("foods").getJSONArray("food").get(0).toString());
                                                saveFoodID.setText(res.getString("food_id"));
                                                foodIDView.setText(getString(R.string.food_display, res.getString("food_name"), res.getString("food_id")));
                                            }
                                            catch (Exception e){}
                                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle a generic error with a generic response
                        Log.d("Error on Volley response", error.toString());
                        foodIDView.setText("Error on url, try again");
                    }
                });
                queue.add(jsonRequest);

                    url = api.getFoodItem(saveFoodID.getText().toString());

                JsonObjectRequest jsonRequest2 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // if all good you can set the values to a variable or just display the results directly.
                        Log.d("Response from FatSecret", response.toString());
//                        displayResults.setText(response.toString());
                        try {
                            Log.d("Response", response.getJSONObject("food").getJSONObject("servings").toString());
                              JSONObject res = new JSONObject(response.getJSONObject("food").getJSONObject("servings").getJSONArray("serving").get(0).toString());
//                            getString(R.string.food_results, res.getString("calories")
                              displayResults.setText(getString(R.string.food_results, res.getString("calories")));
//                            JSONObject nutrition = response.getJSONObject("servings").getJSONObject("serving");
//                            String allNutrition = "Food Name: " + mainFoodName + '\n';
//                            Iterator<String> keys = nutrition.keys();
//                            StringBuilder nutritionText = new StringBuilder();
//                            nutritionText.append(allNutrition);
//                            while (keys.hasNext()) {
//                                Object key = keys.next();
//                                JSONObject value = nutrition.getJSONObject((String) key);
//                                nutritionText.append(foodVariables.get(key));
//                                nutritionText.append(value.toString());
//                                nutritionText.append('\n');
//                            }
//                            allNutrition = nutritionText.toString();
//                            displayResults.setText(allNutrition);
                            //String allNutrition now has the text all set up to be displayed (theoretically)
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error on Volley response", error.toString());
                    }
                });
                // Add the request to the RequestQueue.
//                queue.add(jsonRequest);
                Log.d("Error", saveFoodID.getText().toString());
                Log.d("Error", url);

                queue.add(jsonRequest2);
                // this runs a thread so there's no lag on updating UI
            }
        }
    }
}