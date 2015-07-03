package fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import easset.naviapp.R;

/**
 * Created by easset-01 on 6/16/2015.
 */
public class ViewFragment extends Fragment {
    public static final String ARG_VIEW_FRAGMENT = "view";
    public static final String TITLE = "Sale Vision - View";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_view, container, false);
        return rootView;
    }
}
