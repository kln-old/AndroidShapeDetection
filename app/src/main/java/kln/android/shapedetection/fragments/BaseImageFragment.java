package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import kln.android.shapedetection.MainActivity;
import kln.android.shapedetection.R;

/**
 * Created by kln on 6/23/16.
 */
public class BaseImageFragment extends BaseFragment{

    private static final String TAG = BaseImageFragment.class.getSimpleName();

    private static BaseImageFragment sIntance = null;

    @SuppressLint("ValidFragment")
    private BaseImageFragment(){
        super();
    }

    public static BaseImageFragment getInstance() {
        if (sIntance == null) {
            sIntance = new BaseImageFragment();
        }
        return sIntance;
    }

    @Override
    public void onViewCreated(View view,
                             Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle(getString(R.string.fragment_title_base_image));
        Log.d(TAG, "onViewCreated....");
    }

    @Override
    protected void show() {
        ((MainActivity)getActivity()).showFragmentTab(this);
    }
}
