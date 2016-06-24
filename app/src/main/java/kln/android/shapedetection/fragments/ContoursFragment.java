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
public class ContoursFragment extends BaseFragment {

    private static final String TAG = ContoursFragment.class.getSimpleName();

    private static ContoursFragment sIntance = null;

    @SuppressLint("ValidFragment")
    private ContoursFragment(){
        super();
    }

    public static ContoursFragment getInstance() {
        if (sIntance == null) {
            sIntance = new ContoursFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                              Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.fragment_title_contours));
        Log.d(TAG, "onViewCreated....");
    }

    @Override
    protected void show() {
        ((MainActivity)getActivity()).showFragmentTab(this);
    }
}
