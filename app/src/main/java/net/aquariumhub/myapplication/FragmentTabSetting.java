package net.aquariumhub.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;

import java.util.Calendar;

public class FragmentTabSetting extends Fragment implements com.borax12.materialdaterangepicker.time.TimePickerDialog.OnTimeSetListener, com.borax12.materialdaterangepicker.date.DatePickerDialog.OnDateSetListener {


  Calendar now = Calendar.getInstance();

  com.borax12.materialdaterangepicker.date.DatePickerDialog dpd = com.borax12.materialdaterangepicker.date.DatePickerDialog.newInstance(
          FragmentTabSetting.this,
          now.get(Calendar.YEAR),
          now.get(Calendar.MONTH),
          now.get(Calendar.DAY_OF_MONTH));

  TextView tv_time;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.tab_setting, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {

    super.onViewCreated(view, savedInstanceState);

    // dpd.show(getActivity().getFragmentManager(), "Datepickerdialog");
  }


  @Override
  public void onDateSet(com.borax12.materialdaterangepicker.date.DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {

    String time = "You picked the following time: From ";

  }

  @Override
  public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute, int hourOfDayEnd, int minuteEnd) {
    String hourString = hourOfDay < 10 ? "0"+hourOfDay : ""+hourOfDay;
    String minuteString = minute < 10 ? "0"+minute : ""+minute;
    String hourStringEnd = hourOfDayEnd < 10 ? "0"+hourOfDayEnd : ""+hourOfDayEnd;
    String minuteStringEnd = minuteEnd < 10 ? "0"+minuteEnd : ""+minuteEnd;
    String time = "You picked the following time: From - "+hourString+"h"+minuteString+" To - "+hourStringEnd+"h"+minuteStringEnd;

  }

}
