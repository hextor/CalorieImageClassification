package com.example.monique.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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

import java.io.File;
import java.util.List;



public class MainActivity extends AppCompatActivity implements VolleyListener{

    private ImageView mimageView;
    private static final int REQUEST_IMAGE_CAPTURE= 101;
    private Classifier classifier;

    public String[] permissions = {
        Manifest.permission.CAMERA,
        Manifest.permission.INTERNET,
        Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        for(int i = 0; i < permissions.length-1; i++){
            if (ContextCompat.checkSelfPermission(this, permissions[i])
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted

                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        permissions[i])) {

                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(this,
                            permissions, REQUEST_IMAGE_CAPTURE);
                }
            }
        }
        setContentView(R.layout.activity_main);
        mimageView = findViewById(R.id.imageView);
        try {
            classifier = new ClassifierQuantizedMobileNet(this);
        } catch (Exception e) {
            Log.d("Error on classifier initialization", e.toString());
        }

    }

    public String imageFilePath;
    public Uri imageFileUri;
    public void capture(View view) {
        Intent imageTakeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);



        imageFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/calorieclassification.jpg";
        File imageFile = new File(imageFilePath);
        imageFileUri = FileProvider.getUriForFile(this, "com.example.monique.provider", imageFile); // convert path to Uri
        imageTakeIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);

        if (imageTakeIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(imageTakeIntent, REQUEST_IMAGE_CAPTURE);
        }
    }


    public String url = "";
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        {
            super.onActivityResult(requestCode, resultCode, data);

            final TextView foodIDView = findViewById(R.id.foodIdView);
            final TextView saveFoodID = findViewById(R.id.foodID);

            RequestQueue queue = Volley.newRequestQueue(this);
            GenerateAPI api = new GenerateAPI();
            // After image is taken, it will fall into this statement if successful
            if(requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
//                Bundle extras = data.getExtras();
//                final Bitmap imageBitmap = (Bitmap) extras.get("data");
//                BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
//                bmpFactoryOptions.inJustDecodeBounds = false;
                try{
                    Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageFileUri);
                    mimageView.setImageBitmap(imageBitmap);
                    final List<Classifier.Recognition> results = classifier.recognizeImage(imageBitmap.createScaledBitmap(imageBitmap, 220, 220, false));
                    url = api.searchFoodItem(results.get(0).getTitle());
                }
                catch (Exception e){
                    Log.d("ERROR", e.toString());
                }


//                Log.d("API URL", url);
                JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(final JSONObject response) {
                        // if all good you can set the values to a variable or just display the results directly.
                        Log.d("Response from FatSecret", response.toString());
                                            try{
                                                JSONObject res = new JSONObject(response.getJSONObject("foods").getJSONArray("food").get(0).toString());
                                                saveFoodID.setText(res.getString("food_id"));
                                                foodIDView.setText(getString(R.string.food_display, res.getString("food_name"), res.getString("food_id")));
                                                requestFinished(true);
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
            }
        }
    }

    @Override
    public void requestFinished(boolean done) {
        RequestQueue queue = Volley.newRequestQueue(this);
        GenerateAPI api = new GenerateAPI();
        if (done) {
            final TextView saveFoodID = findViewById(R.id.foodID);
            Log.d("error", saveFoodID.getText().toString());
            final TextView displayResults = findViewById(R.id.calorieTextView);
            url = api.getFoodItem(saveFoodID.getText().toString());
            JsonObjectRequest jsonRequest2 = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.d("Response from FatSecret", response.toString());
                    try {
                        Log.d("Response", response.getJSONObject("food").getJSONObject("servings").toString());
                        JSONObject res = new JSONObject(response.getJSONObject("food").getJSONObject("servings").getJSONArray("serving").get(0).toString());
                        displayResults.setText(getString(R.string.food_results, res.getString("calories")));
//
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error on Volley response", error.toString());
                    displayResults.setText("invalid url" + url);
                }
            });
            Log.d("Error", saveFoodID.getText().toString());
            Log.d("Error", url);
            queue.add(jsonRequest2);

        }
    }
}