package kln.android.shapedetection.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mTitle = (TextView) rootView.findViewById(R.id.section_label);
        mTitle.setText(getString(R.string.fragment_title_default));
        mImageView = (ImageView) rootView.findViewById(R.id.imageView);
        return rootView;
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
        //convert to bitmap & show image
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Bitmap bmp = Bitmap.createBitmap(mat.width(), mat.height(), Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(mat, bmp, true);
                mImageView.setImageBitmap(bmp);
            }
        });
    }

    protected abstract void show();
}
