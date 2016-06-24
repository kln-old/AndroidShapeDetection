package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import kln.android.shapedetection.R;

/**
 * Created by kln on 6/23/16.
 */
public class BaseImageFragment extends BaseFragment{

    private static BaseImageFragment sIntance = null;

    @SuppressLint("ValidFragment")
    private BaseImageFragment(){}

    public static BaseImageFragment getsIntance() {
        if (sIntance == null) {
            sIntance = new BaseImageFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                             Bundle savedInstanceState) {
        setTitle(getString(R.string.fragment_title_base_image));
    }

}
