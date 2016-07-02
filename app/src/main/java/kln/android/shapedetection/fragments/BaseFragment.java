package kln.android.shapedetection.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;

import kln.android.shapedetection.R;

/**
 * Base fragment that provides basic implementations to show our image content
 */
public abstract class BaseFragment extends Fragment{

    /**
     * Title for each page/tab
     */
    private TextView mTitle = null;
    /**
     * Image to be displayed for each tab
     */
    private ImageView mImageView = null;
    /**
     * OpenCv Mat data for the image to be displayed for this tab
     */
    private Mat mMat = null;

    /**
     * Instantiate & setup our components on create
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.section_label);
        mTitle.setText(getString(R.string.fragment_title_default));
        mImageView = (ImageView) rootView.findViewById(R.id.imageView);
        Log.d("KLN", "onViewCreated....");

        return rootView;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // display our image each time this tab is created during scrolls
        displayMatImage();
    }

    /**
     * sets the title for this page/tab
     * @param title
     */
    protected void setTitle(final String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    /**
     * Displays the image from the provided file path
     * @param imagePath
     */
    public void setImage(final String imagePath) {
        if (mImageView != null) {
            // clean
            mImageView.setImageDrawable(null);
            // set image
            mImageView.setImageURI(Uri.fromFile(new File(imagePath)));
        }
    }

    /**
     * Displays the image from the provided Uri
     * @param uri
     */
    public void setImage(final Uri uri) {
        if (mImageView != null) {
            // clean
            mImageView.setImageDrawable(null);
            // set image
            mImageView.setImageURI(uri);
        }
    }

    /**
     * Displays the provided OpenCv Mat by converting it into a bitmap.
     * This method always ensures to be run on UI thread
     * @param mat
     */
    public void showMatImage(final Mat mat) {
        mMat = mat;
        // this may be called from threads other than main thread, so ensure we are always
        // modifying the UI components on UI thread
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // set our image in the fragment
                    displayMatImage();
                    // show our fragment in the tab/view pager
                    show();
                }
            });
        }
    }

    /**
     * Converts our OpenCv Mat into bitmap and set it to our image view
     */
    private void displayMatImage() {
        if (mMat != null) {
            //convert to bitmap & show image
            Bitmap bmp = Bitmap.createBitmap(mMat.width(), mMat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mMat, bmp, true);
            // clean
            mImageView.setImageDrawable(null);
            // show bitmap
            mImageView.setImageBitmap(bmp);
        }
    }

    /**
     * Method that should be implemented to show this fragment on the view pager
     */
    protected abstract void show();
}
