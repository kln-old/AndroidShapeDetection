package kln.android.shapedetection.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import kln.android.shapedetection.MainActivity;
import kln.android.shapedetection.R;

/**
 * Created by kln on 6/24/16.
 */
public class ContoursFragment extends BaseFragment {

    private static final String TAG = ContoursFragment.class.getSimpleName();

    /**
     * Reference to our instance
     */
    private static ContoursFragment sIntance = null;

    // private constructor
    @SuppressLint("ValidFragment")
    private ContoursFragment(){
        super();
    }

    /**
     * Provides the singleton instance of this fragment
     *
     * @return
     */
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
        // set our title
        setTitle(getString(R.string.fragment_title_contours));
    }

    /**
     * Displays this fragment on the view pager
     */
    @Override
    protected void show() {
        ((MainActivity)getActivity()).showFragmentTab(this);
    }
}
