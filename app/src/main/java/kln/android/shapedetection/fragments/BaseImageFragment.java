package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import kln.android.shapedetection.MainActivity;
import kln.android.shapedetection.R;

/**
 * Fragment that shows base image
 */
public class BaseImageFragment extends BaseFragment{

    private static final String TAG = BaseImageFragment.class.getSimpleName();

    /**
     * Reference to our instance
     */
    private static BaseImageFragment sIntance = null;

    // private constructor
    @SuppressLint("ValidFragment")
    private BaseImageFragment(){
        super();
    }

    /**
     * provides the singleton instance of this fragment
     * @return
     */
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
        // set our tab specific title
        setTitle(getString(R.string.fragment_title_base_image));
    }

    /**
     * Displays this fragment on the view pager
     */
    @Override
    protected void show() {
        ((MainActivity)getActivity()).showFragmentTab(this);
    }
}
