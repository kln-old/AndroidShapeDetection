package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import kln.android.shapedetection.MainActivity;
import kln.android.shapedetection.R;

/**
 * Created by kln on 6/24/16.
 */
public class CannyEdgesFragment extends BaseFragment {

    private static final String TAG = CannyEdgesFragment.class.getSimpleName();

    private static CannyEdgesFragment sIntance = null;

    @SuppressLint("ValidFragment")
    private CannyEdgesFragment(){
        super();
    }

    public static CannyEdgesFragment getInstance() {
        if (sIntance == null) {
            sIntance = new CannyEdgesFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.fragment_title_canny_edges));
        Log.d(TAG, "onViewCreated....");
    }

    @Override
    protected void show() {
        ((MainActivity)getActivity()).showFragmentTab(this);
    }
}
