package Utils;

import android.app.DialogFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.widget.DatePicker;

public class DatePickerDialog extends Fragment {
    private DatePickerListener datePickerListener;
    private int listPosition = -1;
    private static final String POSITION_ARG_KEY = "POSITION_ARG_KEY";

    public static DatePickerDialog newInstance(int position) {
        DatePickerDialog fragment = new DatePickerDialog();
        Bundle args = new Bundle();
        args.putInt(POSITION_ARG_KEY, position);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            listPosition = args.getInt(POSITION_ARG_KEY);
        }
    }

    public interface DatePickerListener{
        public void onDateSet(DatePicker view, int year, int month, int day, int listPosition);
    }
}
