package kln.android.shapedetection;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
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
 * Runnable implementationt that performes all OpenCv operations
 */
public class OpenCvRunnable implements Runnable {

    private final static String TAG = OpenCvRunnable.class.getSimpleName();

    /**
     * Path to our base image
     */
    private final String mImagePath;
    /**
     * Kernel size used to blur image
     */
    private int mBlurKernelSize;
    /**
     * minimum threshold value for canny edge detector
     */
    private int mCannyThresholdMin;
    /**
     * Maximum threshold value for canny edge detector
     */
    private int mCannyThresholdMax;
    /**
     * Notifies if we've to use Otsu thresholds for canny edge detector
     */
    private boolean mUseOtsuThreshold;
    /**
     * Type of contours to be detected
     */
    private int mContourType;

    /**
     * Constructor
     * @param imagePath
     * @param blurKernelSize
     * @param cannyThresholdMin
     * @param cannyThresholdMax
     * @param useOtsuThreshold
     * @param contouType
     */
    public OpenCvRunnable(final String imagePath,
                          final int blurKernelSize,
                          final int cannyThresholdMin,
                          final int cannyThresholdMax,
                          final boolean useOtsuThreshold,
                          final int contouType) {

        mImagePath = imagePath;
        mBlurKernelSize = blurKernelSize;
        mCannyThresholdMin = cannyThresholdMin;
        mCannyThresholdMax = cannyThresholdMax;
        mUseOtsuThreshold = useOtsuThreshold;
        switch (contouType) {
            case 0:
                mContourType = Imgproc.RETR_EXTERNAL;
                break;
            case 1:
                mContourType = Imgproc.RETR_LIST;
                break;
            case 2:
                mContourType = Imgproc.RETR_CCOMP;
                break;
            case 3:
                mContourType = Imgproc.RETR_TREE;
                break;
            case 4:
                mContourType = Imgproc.RETR_FLOODFILL;
                break;
            default:
                mContourType = Imgproc.RETR_EXTERNAL;
        }
    }

    /**
     * Runnable core
     */
    @Override
    public void run() {
        final Mat orignalImage = Imgcodecs.imread(mImagePath);
        Mat finalMat = new Mat(orignalImage.rows(), orignalImage.cols(), orignalImage.type());
        Mat tmpImage = new Mat();
        Mat detectedEdges = new Mat();
        final Mat hierarchy = new Mat();
        final List<MatOfPoint> contours = new ArrayList<>();
        Scalar red = new Scalar(255, 0, 0);
        Scalar blue = new Scalar(0, 0, 255);
        Scalar green = new Scalar(0, 255, 0);
        Scalar white = new Scalar(255, 255, 255);
        Scalar yellow = new Scalar(255, 222, 0);
        Scalar black = new Scalar(0, 0, 0);

        // show original image
        BaseImageFragment.getInstance().showMatImage(orignalImage);

        // convert to grey scale
        Imgproc.cvtColor(orignalImage, tmpImage, Imgproc.COLOR_RGBA2GRAY);

        // blur image & show
        Imgproc.blur(tmpImage, tmpImage, new Size(mBlurKernelSize, mBlurKernelSize));
        BlurImageFragment.getInstance().showMatImage(tmpImage);

        // use threshold as set by preferences
        double maxThreshold = mCannyThresholdMax * 1.0;
        double minThreshold = mCannyThresholdMin * 1.0;
        // if ostu threshold is enabled, then use it instead
        if (mUseOtsuThreshold) {
            Mat tmp2 = new Mat();
            double otsuThreshold = Imgproc.threshold(tmpImage, tmp2, (double) 0, (double) 255, Imgproc.THRESH_BINARY | Imgproc.THRESH_OTSU);
            maxThreshold = otsuThreshold;
            minThreshold = otsuThreshold * 0.5;
        }
        // detect & show edges
        Imgproc.Canny(tmpImage, detectedEdges, minThreshold, maxThreshold);
        CannyEdgesFragment.getInstance().showMatImage(detectedEdges);

        // find contours
        Imgproc.findContours(detectedEdges, contours, hierarchy, mContourType, Imgproc.CHAIN_APPROX_NONE);

        // iterate over all identified contours and analyze them
        MatOfPoint2f contour2f;
        for (int i = 0; i < contours.size(); i++) {
            // do some approximations to the idenitfied curves
            contour2f = new MatOfPoint2f(contours.get(i).toArray());
            double approxDistance = 0.03 * Imgproc.arcLength(contour2f, true);
            MatOfPoint2f approxCurve2f = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approxCurve2f, approxDistance, true);
            MatOfPoint approxCurve = new MatOfPoint();
            approxCurve2f.convertTo(approxCurve, CvType.CV_32S);

            // skip small areas
            if (Math.abs(Imgproc.contourArea(contour2f)) < 3000) {
                Log.d(TAG, "small shape...skipping");
                // show for debugging visualization
                Imgproc.drawContours(finalMat, contours, i, white, 3, 8, hierarchy, 0, new Point(0, 0));
                ContoursFragment.getInstance().showMatImage(finalMat);
                continue;
            }

            // skip if concave
            if (!Imgproc.isContourConvex(approxCurve)) {
                Log.d(TAG, "concave shape...skipping");
                // show for debugging visualization
                Imgproc.drawContours(finalMat, contours, i, green, 3, 8, hierarchy, 0, new Point(0, 0));
                ContoursFragment.getInstance().showMatImage(finalMat);
                continue;
            }

            // filter based on number of vertices
            // Log.d(TAG, "number of points = " + approxCurve.total());
            int vertices = approxCurve.height();
            Log.d(TAG, "Vertices = " + vertices);
            if (vertices == 4) {
                Log.d(TAG, "Found rectangle");
                //Imgproc.drawContours(detectedEdges, contours, i, white, -1);
                Imgproc.drawContours(finalMat, contours, i, red, 3, 8, hierarchy, 0, new Point(0, 0));
                // draw bounding rectangle around our selected contour
                Rect boundingRect = Imgproc.boundingRect(approxCurve);
                Imgproc.rectangle(finalMat, new Point(boundingRect.x - 5 , boundingRect.y - 5),
                        new Point(boundingRect.x + boundingRect.width + 5 , boundingRect.y + boundingRect.height + 5),
                        yellow, 3);
                ContoursFragment.getInstance().showMatImage(finalMat);
            } else {
                Imgproc.drawContours(finalMat, contours, i, blue, 3, 8, hierarchy, 0, new Point(0, 0));
                ContoursFragment.getInstance().showMatImage(finalMat);
                Log.d(TAG, "skipping vertices = " + vertices);
            }
        }
    }
}
