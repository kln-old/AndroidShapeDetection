package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import kln.android.shapedetection.R;

/**
 * Created by kln on 6/24/16.
 */
public class CannyEdgesFragment extends BaseFragment {

    private static CannyEdgesFragment sIntance = null;

    @SuppressLint("ValidFragment")
    private CannyEdgesFragment(){}

    public static CannyEdgesFragment getsIntance() {
        if (sIntance == null) {
            sIntance = new CannyEdgesFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        setTitle(getString(R.string.fragment_title_canny_edges));
    }

}
