package com.example.viet.demoopencv;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    ImageView ivSrc, ivDes;
    private int[] id = {R.drawable.image_0, R.drawable.image1, R.drawable.image_2, R.drawable.image_3, R.drawable.image_4, R.drawable.image_5,
            R.drawable.image_6, R.drawable.image_7, R.drawable.image_8, R.drawable.image_9, R.drawable.image_10};
    private ArrayList<Mat> arrMat = new ArrayList<>();

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "failed");

        } else {
            Log.d(TAG, "Successful");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int i = 0; i < id.length; i++) {
            arrMat.add(bitmap2Mat(BitmapFactory.decodeResource(getResources(), id[i])));
        }
        initViews();
        Mat mat = arrMat.get(0);
        Mat threshold = processing(mat);
        ivSrc.setImageBitmap(mat2Bitmap(mat));
        ivDes.setImageBitmap(mat2Bitmap(threshold));
    }

    private Mat processing(Mat input) {
        Mat inputClone = input.clone();
        Mat threshold = new Mat();
        Mat gray = new Mat();
        Imgproc.cvtColor(input, gray, Imgproc.COLOR_RGB2GRAY);
        Imgproc.adaptiveThreshold(gray, threshold, 255, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 15, 2);
        Imgproc.medianBlur(threshold, threshold, 5);
        multiDilate(threshold, 10);
        Mat canny = new Mat();
        Imgproc.Canny(threshold, canny, 200, 100);
        multiDilate(canny, 5);
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(canny, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.println("Size : " + contours.size());
        System.out.println("Size : " + hierarchy.cols());
        float xCenter = 0, yCenter = 0;
        int totalPoint = 0;
        for (int idx = 0; idx < contours.size(); idx++) {
            Point[] arrPoint = contours.get(idx).toArray();

            for (int i = 0; i < arrPoint.length; i++) {
                xCenter += arrPoint[i].x;
                yCenter += arrPoint[i].y;
                System.out.print("(" + arrPoint[i].x + "," + arrPoint[i].y + ")");
            }
            totalPoint += arrPoint.length;

            System.out.println("");
            if (Imgproc.contourArea(contours.get(idx)) < 1000) continue;
            Imgproc.drawContours(inputClone, contours, idx, new Scalar(255, 0, 0), 0);
        }
        xCenter = xCenter / totalPoint;
        yCenter = yCenter / totalPoint;
        System.out.println("CENTER : (" + xCenter + "," + yCenter + ")");
        Imgproc.drawMarker(inputClone, new Point(xCenter, yCenter), new Scalar(255, 0, 0));
        return inputClone;
    }

    private void multiDilate(Mat mat, int numberLoop) {
        for (int i = 0; i < numberLoop; i++) {
            dilate(mat);
        }
    }

    private void dilate(Mat mat) {
        Imgproc.dilate(mat, mat, new Mat());
    }

    private void erode(Mat mat) {
        Imgproc.erode(mat, mat, new Mat());
    }

    private Mat getHChanel(Mat input) {
        List<Mat> chanels = new ArrayList();
        Core.split(input, chanels);
        Mat h = chanels.get(0);
        Mat s = chanels.get(1);
        Mat v = chanels.get(2);
        return h;
    }

    private Mat colorFiltering(Mat input) {
        Mat hsv = new Mat();
        Imgproc.cvtColor(input, hsv, Imgproc.COLOR_BGR2HSV);
        Mat mask = new Mat();
        Core.inRange(hsv, new Scalar(0, 0, 205), new Scalar(180, 10, 255), mask);
        return mask;
    }

    private Bitmap mat2Bitmap(Mat mat) {
        Bitmap bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        return bitmap;
    }

    private Mat bitmap2Mat(Bitmap bmp) {
        Bitmap bmp32 = bmp.copy(Bitmap.Config.ARGB_8888, true);
        Mat tmp = new Mat(bmp32.getWidth(), bmp32.getHeight(), CvType.CV_8UC1);
        Utils.bitmapToMat(bmp32, tmp);
        return tmp;
    }

    private void initViews() {
        ivSrc = findViewById(R.id.ivSrc);
        ivDes = findViewById(R.id.ivDes);
    }
}
