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

import org.json.JSONObject;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ImageView mimageView;
    private static final int REQUEST_IMAGE_CAPTURE= 101;
    private Runnable recognizer;
    private Classifier classifier;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

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
                // run in debug mode and hover over this line to see data details.
                Log.d("Results for image", results.toString());  // GET THE FIRST KEYWORD -- results.get(0).getTitle()

                GenerateAPI api = new GenerateAPI();

                // Instantiate the RequestQueue.
                RequestQueue queue = Volley.newRequestQueue(this);

                // Examples of how to use the api fields.
                // Need to create the urls to be used in the JsonObjectRequest functions
                // Request a json response from the provided URL.
                String url = api.searchFoodItem("apple");
                // This ID is for apple.
                String descriptionUrl = api.getFoodItem("35718");


                Log.d("API URL", url);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // if all good you can set the values to a variable or just display the results directly.
                        Log.d("Response from FatSecret", response.toString());
//                        displayResults.setText(response.toString());

                    }
                    }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle a generic error with a generic response
                        Log.d("Error on Volley response", error.toString());
                    }
                });
                queue.add(jsonRequest);

                JsonObjectRequest jsonRequest2 = new JsonObjectRequest(Request.Method.GET, descriptionUrl, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // if all good you can set the values to a variable or just display the results directly.
                        Log.d("Response from FatSecret", response.toString());
                        displayResults.setText(response.toString());

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle a generic error with a generic response
                        Log.d("Error on Volley response", error.toString());
                    }
                });

                // Add the request to the RequestQueue.
                queue.add(jsonRequest2);


                // this runs a thread so there's no lag on updating UI
//                runOnUiThread(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                // setting it to a proper percentage for display
//                                String confidence = String.format(Locale.ENGLISH, "%f%%", results.get(0).getConfidence() * 100);
//                                // set it with our classification results with a formatted string inside res/values/strings.xml
//                                displayResults.setText(getString(R.string.item_guess, results.get(0).getTitle(), confidence));
//                                // create new textViews and set text for the information from api call.
//                            }
//                        }
//                );
//
            }
        }
    }
}
