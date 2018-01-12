package com.example.viet.demoopencv;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class Main2Activity extends AppCompatActivity {
    ImageView ivCroppedBitmap;

    private int[] id = {R.drawable.image_1, R.drawable.image_2, R.drawable.image_2, R.drawable.image_3,
            R.drawable.image_4, R.drawable.image_5, R.drawable.image_6, R.drawable.image_7,
            R.drawable.image_8, R.drawable.image_9, R.drawable.image_10};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }
        initViews();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), id[3]);
        Bitmap cutBitmap = cutBitmap(bitmap, 500, 300, 500, 500);
        ivCroppedBitmap.setImageBitmap(cutBitmap);

        try {
            saveBitmap(cutBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveBitmap(Bitmap bitmap) throws Exception {
        File file = new File(Environment.getExternalStorageDirectory() + "/image.jpg");
        Toast.makeText(this, Environment.getExternalStorageDirectory() + "/image.jpg", Toast.LENGTH_SHORT).show();
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream outputStream = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

    }

    private void initViews() {
        ivCroppedBitmap = findViewById(R.id.ivCroppedBitmap);
    }

    private Bitmap cutBitmap(Bitmap originalBitmap, int x, int y, int width, int height) {
        Bitmap cutBitmap = Bitmap.createBitmap(width,
                height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(cutBitmap);
        Rect srcRect = new Rect(x, y, x + width, y + height);
        Rect desRect = new Rect(0, 0, width, height);
        canvas.drawBitmap(originalBitmap, srcRect, desRect, null);
        return cutBitmap;
    }
}
