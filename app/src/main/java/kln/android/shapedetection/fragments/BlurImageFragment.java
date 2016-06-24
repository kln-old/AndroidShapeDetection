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
public class BlurImageFragment extends BaseFragment {

    private static final String TAG = BlurImageFragment.class.getSimpleName();

    private static BlurImageFragment sIntance = null;

    @SuppressLint("ValidFragment")
    private BlurImageFragment(){
        super();
    }

    public static BlurImageFragment getInstance() {
        if (sIntance == null) {
            sIntance = new BlurImageFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.fragment_title_blur_image));
        Log.d(TAG, "onViewCreated....");
    }

    @Override
    protected void show() {
        ((MainActivity)getActivity()).showFragmentTab(this);
    }
}
