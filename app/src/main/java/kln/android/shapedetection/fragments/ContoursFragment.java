package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import kln.android.shapedetection.R;

/**
 * Created by kln on 6/24/16.
 */
public class ContoursFragment extends BaseFragment {

    private static ContoursFragment sIntance = null;

    @SuppressLint("ValidFragment")
    private ContoursFragment(){}

    public static ContoursFragment getsIntance() {
        if (sIntance == null) {
            sIntance = new ContoursFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        setTitle(getString(R.string.fragment_title_contours));
    }

}
