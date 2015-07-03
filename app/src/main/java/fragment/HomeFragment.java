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
public class HomeFragment extends Fragment{
    public static final String ARG_HOME_FRAGMENT = "home";
    public static final String TITLE = "Sale Vision - Home";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }
}
