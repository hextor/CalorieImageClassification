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

import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {


    private ImageView mimageView;
    private static final int REQUEST_IMAGE_CAPTURE= 1;
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
                Log.d("Results for image", results.toString());
                // GET THE FIRST KEYWORD -- and set it with our formatted string inside res/values/strings.xml
                runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                String confidence = String.format(Locale.ENGLISH, "%f%%", results.get(0).getConfidence() * 100);
                                displayResults.setText(getString(R.string.item_guess, results.get(0).getTitle(), confidence));
                                // THEN MAKE THE URL REQUEST
                            }
                        }
                );
//                // Instantiate the RequestQueue.
//                RequestQueue queue = Volley.newRequestQueue(this);
//                String url ="http://www.google.com";
//                // Request a string response from the provided URL.
//                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
//                        new Response.Listener<String>() {
//                            @Override
//                            public void onResponse(String response) {
//                                // Display the first 500 characters of the response string.
//                              //  textView.setText("Response is: "+ response.substring(0,500));
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        //textView.setText("That didn't work!");
//                    }
//                });
//                // Add the request to the RequestQueue.
//                queue.add(stringRequest);
            }
        }
    }
}
