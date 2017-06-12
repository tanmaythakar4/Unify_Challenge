package com.example.tanut.unifyid;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Base64;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends Activity implements View.OnClickListener
{
    private Button btn_click;
    private Camera mCamera;
    private SurfaceView view;
    private ArrayList<String> mEncodedBitList;
    private int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            btn_click = (Button)findViewById(R.id.btn_click);
            btn_click.setOnClickListener(this);
            mEncodedBitList = new ArrayList<>();

    }

    @Override
    public void onClick(View v) {
        view = new SurfaceView(this);
        takePicture(10,500);
    }

    private void takePicture(final int i, final int t) {
        // i = number of photo
        // t = interval
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)){
                Thread thread = new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        while(count<i){
                            try {
                                mCamera = Camera.open();
                                mCamera.setPreviewDisplay(view.getHolder());
                                mCamera.startPreview();
                                mCamera.takePicture(null,null,customCallback);
                                count++;
                                sleep(t);
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                thread.start();
        }
        else{
            // no camera
            Toast.makeText(this,"Not supported",Toast.LENGTH_SHORT).show();
        }
    }


    Camera.PictureCallback customCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] bytes, Camera camera) {
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
            String encodedBitmap = encodeToBase64(bitmap,Bitmap.CompressFormat.JPEG, 100);
            mEncodedBitList.add(encodedBitmap);
            // Store this list in local database or in shared Prefrence

        }
    };
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
}
