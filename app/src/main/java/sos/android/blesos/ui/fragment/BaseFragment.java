package sos.android.blesos.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by soorianarayanan on 2/17/2016.
 */
public class BaseFragment extends Fragment {
    protected Activity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mActivity = (Activity) context;
    }
}
