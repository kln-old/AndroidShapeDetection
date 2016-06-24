package kln.android.shapedetection.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

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

    protected void setImage(final String imagePath) {
        if (mImageView != null) {
            mImageView.setImageURI(Uri.fromFile(new File(imagePath)));
        }
    }
}
