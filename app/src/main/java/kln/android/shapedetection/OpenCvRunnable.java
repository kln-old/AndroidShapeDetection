package kln.android.shapedetection;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

import kln.android.shapedetection.fragments.BaseImageFragment;
import kln.android.shapedetection.fragments.BlurImageFragment;
import kln.android.shapedetection.fragments.CannyEdgesFragment;
import kln.android.shapedetection.fragments.ContoursFragment;

/**
 * Created by kln on 6/24/16.
 */
public class OpenCvRunnable implements Runnable {

    private final static String TAG = OpenCvRunnable.class.getSimpleName();
    private final String mImagePath;

    public OpenCvRunnable(final String imagePath) {
        mImagePath = imagePath;
    }

    @Override
    public void run() {
        Mat orignalImage = Imgcodecs.imread(mImagePath);
        Mat tmpImage = new Mat();
        Mat detectedEdges = new Mat();
        final List<MatOfPoint> contours = new ArrayList<>();
        final Mat hierarchy = new Mat();
        Scalar red = new Scalar(255, 0, 0);
        Scalar blue = new Scalar(0, 255, 0);
        Scalar green = new Scalar(0, 0, 255);
        Scalar white = new Scalar(255, 255, 255);
        Scalar black = new Scalar(0, 0, 0);
        Mat finalMat = new Mat(orignalImage.rows(), orignalImage.cols(), orignalImage.type());

        // show original image
        BaseImageFragment.getInstance().showMatImage(orignalImage);

        // convert to grey scale
        Imgproc.cvtColor(orignalImage, tmpImage, Imgproc.COLOR_RGBA2GRAY);
        // blur image
        Imgproc.blur(tmpImage, tmpImage, new Size(3,3));
        BlurImageFragment.getInstance().showMatImage(tmpImage);
        Mat tmp2 = new Mat();
        // detect edges
        double otsuThreshold = Imgproc.threshold(tmpImage, tmp2, (double)0, (double)255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
        double maxThreshold = otsuThreshold;
        double minThreshold = otsuThreshold * 0.5;
        Imgproc.Canny(tmpImage, detectedEdges,30, 100);
        CannyEdgesFragment.getInstance().showMatImage(detectedEdges);
        // find contours
        Imgproc.findContours(detectedEdges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        // find rectangles
        MatOfPoint2f contour2f;
        for (int i=0; i < contours.size(); i++) {
            contour2f = new MatOfPoint2f(contours.get(i).toArray());
            double approxDistance = 0.05 * Imgproc.arcLength(contour2f, true);
            MatOfPoint2f approxCurve2f = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approxCurve2f, approxDistance, true);
            MatOfPoint approxCurve = new MatOfPoint();
            approxCurve2f.convertTo(approxCurve, CvType.CV_32S);

            // skip small or non-convex curves
            if (Math.abs(Imgproc.contourArea(contour2f)) < 3000) {
                Log.d(TAG, "small shape...skipping");
                Imgproc.drawContours(finalMat, contours, i, white, 3, 8, hierarchy, 0, new Point(0,0));
                ContoursFragment.getInstance().showMatImage(finalMat);
                continue;
            }

            if (!Imgproc.isContourConvex(approxCurve)) {
                Log.d(TAG, "concave shape...skipping");
                Imgproc.drawContours(finalMat, contours, i, green, 3, 8, hierarchy, 0, new Point(0,0));
                ContoursFragment.getInstance().showMatImage(finalMat);
                continue;
            }
            int vertices = approxCurve.height();
            Log.d(TAG, "Vertices = " + vertices);
            if ( vertices == 4) {
                Log.d(TAG, "Found rectangle");
                //Imgproc.drawContours(detectedEdges, contours, i, white, -1);
                Imgproc.drawContours(finalMat, contours, i, red, 3, 8, hierarchy, 0, new Point(0,0));
                ContoursFragment.getInstance().showMatImage(finalMat);
            } else {
                Imgproc.drawContours(finalMat, contours, i, blue, 3, 8, hierarchy, 0, new Point(0,0));
                ContoursFragment.getInstance().showMatImage(finalMat);
                Log.d(TAG, "skipping vertices = " + vertices);
            }
        }


        /*
        String resultImagePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "edges.png";
        if (Imgcodecs.imwrite(resultImagePath, detectedEdges)){
            Log.i(TAG, "result image path - " + resultImagePath);
            imageView.setImageDrawable(null);
            imageView.setImageURI(Uri.fromFile(new File(resultImagePath)));
        } else {
            Log.e(TAG, "Failed to write image");
        }
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(resultImagePath)), "image/*");
        startActivity(intent);
        */

    }
}
