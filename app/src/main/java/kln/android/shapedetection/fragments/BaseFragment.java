package kln.android.shapedetection.fragments;

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
 * Created by kln on 6/24/16.
 */
public abstract class BaseFragment extends Fragment{

    private TextView mTitle = null;
    private ImageView mImageView = null;
    private Mat mMat = null;

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
        displayMatImage();
    }

    protected void setTitle(final String title) {
        if (mTitle != null) {
            mTitle.setText(title);
        }
    }

    public void setImage(final String imagePath) {
        if (mImageView != null) {
            mImageView.setImageURI(Uri.fromFile(new File(imagePath)));
        }
    }

    public void setImage(final Uri uri) {
        if (mImageView != null) {
            mImageView.setImageURI(uri);
        }
    }

    public void showMatImage(final Mat mat) {
        mMat = mat;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                displayMatImage();
                show();
            }
        });
    }

    private void displayMatImage() {
        if (mMat != null) {
            //convert to bitmap & show image
            Bitmap bmp = Bitmap.createBitmap(mMat.width(), mMat.height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(mMat, bmp, true);
            mImageView.setImageBitmap(bmp);
        }
    }

    protected abstract void show();
}
