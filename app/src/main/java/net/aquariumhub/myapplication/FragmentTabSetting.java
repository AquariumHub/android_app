package net.aquariumhub.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;

import java.util.Calendar;

public class FragmentTabSetting extends Fragment {

  final String TAG = "FragmentTabSetting";

  ImageButton setting_edit_temp;
  ImageButton setting_edit_bright;
  ImageButton setting_edit_frequency;
  TextView textViewTempUpperBound;
  TextView textViewTempLowerBound;

  View view_dialog_temp;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.tab_setting, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    view_dialog_temp = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_temp_rule, null);

    textViewTempUpperBound = getActivity().findViewById(R.id.setting_temp_upperValue);
    textViewTempLowerBound = getActivity().findViewById(R.id.setting_temp_lowerValue);

    setting_edit_temp = getActivity().findViewById(R.id.setting_edit_temp);
    setting_edit_bright = getActivity().findViewById(R.id.setting_edit_bright);
    setting_edit_frequency = getActivity().findViewById(R.id.setting_edit_frequency);

    setting_edit_temp.setOnClickListener(setting_edit_temp_clicked);
    setting_edit_bright.setOnClickListener(setting_edit_bright_clicked);
    setting_edit_frequency.setOnClickListener(setting_edit_frequency_clicked);
  }

  ImageButton.OnClickListener setting_edit_temp_clicked = new ImageButton.OnClickListener() {

      @Override
      public void onClick (View view){
      //產生視窗物件
        try{
      new AlertDialog.Builder(getActivity())
              .setTitle("Setting Temp Alert")//設定視窗標題
              .setView(view_dialog_temp)
              // set the action buttons
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  int iTempUpperBound;
                  int iTempLowerBound;
                  EditText editTextUpperBound = view_dialog_temp.findViewById(R.id.editText_temp_upperBound);
                  EditText editTextLowerBound = view_dialog_temp.findViewById(R.id.editText_temp_lowerBound);
                  editTextUpperBound.setHint(textViewTempUpperBound.getText().toString());
                  editTextLowerBound.setHint(textViewTempLowerBound.getText().toString());

                  if (editTextUpperBound.length() > 0 && editTextLowerBound.length() > 0) {
                    iTempUpperBound = Integer.parseInt(editTextUpperBound.getText().toString());
                    iTempLowerBound = Integer.parseInt(editTextLowerBound.getText().toString());
                    if (iTempLowerBound > iTempUpperBound)
                      Toast.makeText(getActivity(), "Upper bound must bigger than lower bound", Toast.LENGTH_LONG).show();
                    else {
                      textViewTempUpperBound.setText(Integer.toString(iTempUpperBound));
                      textViewTempLowerBound.setText(Integer.toString(iTempLowerBound));
                      dialog.cancel();
                    }
                  } else {
                    if (editTextUpperBound.length() > 0) {
                      textViewTempUpperBound.setText(editTextUpperBound.getText().toString());
                    }
                    if (editTextLowerBound.length() > 0) {
                      textViewTempUpperBound.setText(editTextLowerBound.getText().toString());
                    }

                  }

                }
              })
              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .show();//呈現對話視窗
        } catch (Exception e){
          Log.e(TAG, "OnClick", e);
        }
    }
  };

  ImageButton.OnClickListener setting_edit_bright_clicked = new ImageButton.OnClickListener() {
    @Override
    public void onClick(View view) {
      //產生視窗物件
      new AlertDialog.Builder(getActivity())
              .setTitle("Setting Temp Alert")//設定視窗標題
              .setView(R.layout.dialog_bright_rule)
              // set the action buttons
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .show();//呈現對話視窗
    }
  };

  ImageButton.OnClickListener setting_edit_frequency_clicked = new ImageButton.OnClickListener() {
    @Override
    public void onClick(View view) {
      //產生視窗物件
      new AlertDialog.Builder(getActivity())
              .setTitle("Setting Temp Alert")//設定視窗標題
              .setView(R.layout.dialog_freq_rule)
              // set the action buttons
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .show();//呈現對話視窗
    }
  };

}
