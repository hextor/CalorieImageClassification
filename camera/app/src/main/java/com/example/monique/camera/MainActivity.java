package com.example.monique.camera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        {
            // After image is taken, it will fall into this statement if successful
            if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                final Bitmap imageBitmap = (Bitmap) extras.get("data");
                mimageView.setImageBitmap(imageBitmap);

                final List<Classifier.Recognition> results = classifier.recognizeImage(imageBitmap);
                // run in debug mode and hover over this line to see data details.
                Log.d("Results for image", results.toString());
            }
        }
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


}
