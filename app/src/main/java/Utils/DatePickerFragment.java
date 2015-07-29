package Utils;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.DatePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener{
    public onDateChangeListener mListener;
    private int listPosition = -1;
    private int yearT;
    private int monthT;
    private int dayT;

    private static final String POSITION_ARG_KEY = "POSITION_ARG_KEY";
    private static final String DATE_ARG_KEY = "DATE_ARG_KEY";
    private static final String MOUTH_ARG_KEY = "MOUTH_ARG_KEY";
    private static final String YEAR_ARG_KEY = "YEAR_ARG_KEY";


    public static DatePickerFragment newInstance(int position, int year, int month, int day) {
        DatePickerFragment fragment = new DatePickerFragment();
        Bundle args = new Bundle();
        args.putInt(POSITION_ARG_KEY, position);
        args.putInt(DATE_ARG_KEY, day);
        args.putInt(MOUTH_ARG_KEY, month);
        args.putInt(YEAR_ARG_KEY, year);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            listPosition = args.getInt(POSITION_ARG_KEY);
            dayT = args.getInt(DATE_ARG_KEY);
            monthT = args.getInt(MOUTH_ARG_KEY);
            yearT = args.getInt(YEAR_ARG_KEY);

        }
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (onDateChangeListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement onDateChangeListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        int month = monthOfYear + 1;
        String formattedMonth = "" + month;
        String formattedDayOfMonth = "" + dayOfMonth;

        if(month < 10){

            formattedMonth = "0" + month;
        }
        if(dayOfMonth < 10){

            formattedDayOfMonth = "0" + dayOfMonth;
        }
        String changedDate =  formattedDayOfMonth + "-" + formattedMonth + "-" + year;

        if(mListener!=null){
            mListener.sendDateBack(changedDate, listPosition);
        }

    }

    //Listener for adapter
    public interface onDateChangeListener{
        void sendDateBack(String changedDate, int listPosition);
    }

}
