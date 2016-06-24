package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import kln.android.shapedetection.R;

/**
 * Created by kln on 6/24/16.
 */
public class BlurImageFragment extends BaseFragment {

    private static BlurImageFragment sIntance = null;

    @SuppressLint("ValidFragment")
    private BlurImageFragment(){}

    public static BlurImageFragment getsIntance() {
        if (sIntance == null) {
            sIntance = new BlurImageFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        setTitle(getString(R.string.fragment_title_blur_image));
    }

}
