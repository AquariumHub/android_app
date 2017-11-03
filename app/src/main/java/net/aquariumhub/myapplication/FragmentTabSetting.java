package net.aquariumhub.myapplication;

import android.app.Activity;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;

import java.util.Calendar;

import static android.content.Context.BIND_AUTO_CREATE;

public class FragmentTabSetting extends Fragment {

  final String TAG = "FragmentTabSetting";

  private AwsService myService;
  boolean mBounded;

  ImageButton setting_edit_temp;
  ImageButton setting_edit_bright;
  ImageButton setting_edit_frequency;
  TextView textViewTempUpperBound;
  TextView textViewTempLowerBound;
  TextView textViewBrightUpperBound;
  TextView textViewBrightLowerBound;
  TextView textViewFreqUpperBound;
  TextView textViewFreqLowerBound;

  ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      //Toast.makeText(getActivity(), "Service is connected", Toast.LENGTH_LONG).show();
      AwsService.MyBinder myBinder = (AwsService.MyBinder) service;
      myService = myBinder.getAwsServiceInstance();
      mBounded = true;
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      //Toast.makeText(getActivity(), "Service is disconnected", Toast.LENGTH_LONG).show();
      mBounded = false;
      myService = null;
    }
  };

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.tab_setting, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    textViewTempUpperBound = getActivity().findViewById(R.id.setting_temp_upperValue);
    textViewTempLowerBound = getActivity().findViewById(R.id.setting_temp_lowerValue);

    textViewBrightUpperBound = getActivity().findViewById(R.id.setting_bright_upperValue);
    textViewBrightLowerBound = getActivity().findViewById(R.id.setting_bright_lowerValue);

    textViewFreqUpperBound = getActivity().findViewById(R.id.setting_frequency_upperValue);
    textViewFreqLowerBound = getActivity().findViewById(R.id.setting_frequency_lowerValue);

    setting_edit_temp = getActivity().findViewById(R.id.setting_edit_temp);
    setting_edit_bright = getActivity().findViewById(R.id.setting_edit_bright);
    setting_edit_frequency = getActivity().findViewById(R.id.setting_edit_frequency);

    setting_edit_temp.setOnClickListener(setting_edit_temp_clicked);
    setting_edit_bright.setOnClickListener(setting_edit_bright_clicked);
    setting_edit_frequency.setOnClickListener(setting_edit_frequency_clicked);
  }

  @Override
  public void onStart() {
    super.onStart();
    Intent myIntent = new Intent(getActivity(), AwsService.class);
    getActivity().bindService(myIntent, serviceConnection, BIND_AUTO_CREATE);
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mBounded) {
      getActivity().unbindService(serviceConnection);
      mBounded = false;
    }
  }

  ImageButton.OnClickListener setting_edit_temp_clicked = new ImageButton.OnClickListener() {

    @Override
    public void onClick(final View view) {
      //產生視窗物件
      final View view_dialog_temp = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_temp_rule, null);
      new AlertDialog.Builder(getActivity())
              .setTitle("Setting Temperature Alert")//設定視窗標題
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
                      textViewTempLowerBound.setText(Integer.toString(iTempLowerBound));
                      textViewTempUpperBound.setText(Integer.toString(iTempUpperBound));
                      myService.tempLowerBound = iTempLowerBound;
                      myService.tempUpperBound = iTempUpperBound;
                    }
                  } else {
                    if (editTextUpperBound.length() > 0) {
                      textViewTempUpperBound.setText(editTextUpperBound.getText().toString());
                      myService.tempUpperBound = Integer.parseInt(editTextUpperBound.getText().toString());
                      myService.tempLowerBound = 0;
                    }
                    if (editTextLowerBound.length() > 0) {
                      textViewTempLowerBound.setText(editTextLowerBound.getText().toString());
                      myService.tempLowerBound = Integer.parseInt(editTextLowerBound.getText().toString());
                      myService.tempUpperBound = 100;
                    }
                  }
                  dialog.cancel();
                }
              })
              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .setNeutralButton("Reset", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  myService.tempLowerBound = 0;
                  myService.tempUpperBound = 100;
                  textViewTempLowerBound.setText("0");
                  textViewTempUpperBound.setText("100");
                  dialog.cancel();
                }
              })
              .show();//呈現對話視窗
    }
  };

  ImageButton.OnClickListener setting_edit_bright_clicked = new ImageButton.OnClickListener() {
    @Override
    public void onClick(View view) {
      //產生視窗物件
      final View view_dialog_bright = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_bright_rule, null);
      new AlertDialog.Builder(getActivity())
              .setTitle("Setting Brightness Alert")//設定視窗標題
              .setView(view_dialog_bright)
              // set the action buttons
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  int iBrightUpperBound;
                  int iBrightLowerBound;
                  EditText editTextUpperBound = view_dialog_bright.findViewById(R.id.editText_bright_upperBound);
                  EditText editTextLowerBound = view_dialog_bright.findViewById(R.id.editText_bright_lowerBound);
                  editTextUpperBound.setHint(textViewBrightUpperBound.getText().toString());
                  editTextLowerBound.setHint(textViewBrightLowerBound.getText().toString());

                  if (editTextUpperBound.length() > 0 && editTextLowerBound.length() > 0) {
                    iBrightUpperBound = Integer.parseInt(editTextUpperBound.getText().toString());
                    iBrightLowerBound = Integer.parseInt(editTextLowerBound.getText().toString());
                    if (iBrightLowerBound > iBrightUpperBound)
                      Toast.makeText(getActivity(), "Upper bound must bigger than lower bound", Toast.LENGTH_LONG).show();
                    else {
                      textViewBrightLowerBound.setText(Integer.toString(iBrightLowerBound));
                      textViewBrightUpperBound.setText(Integer.toString(iBrightUpperBound));
                      myService.brightLowerBound = iBrightLowerBound;
                      myService.brightUpperBound = iBrightUpperBound;
                    }
                  } else {
                    if (editTextUpperBound.length() > 0) {
                      textViewBrightUpperBound.setText(editTextUpperBound.getText().toString());
                      myService.brightUpperBound = Integer.parseInt(editTextUpperBound.getText().toString());
                      myService.brightLowerBound = 0;
                    }
                    if (editTextLowerBound.length() > 0) {
                      textViewBrightLowerBound.setText(editTextLowerBound.getText().toString());
                      myService.brightLowerBound = Integer.parseInt(editTextLowerBound.getText().toString());
                      myService.brightUpperBound = 1000;
                    }
                  }
                  dialog.cancel();
                }
              })
              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .setNeutralButton("Reset", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  myService.brightLowerBound = 0;
                  myService.brightUpperBound = 1000;
                  textViewBrightLowerBound.setText("0");
                  textViewBrightUpperBound.setText("1000");
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
      final View view_dialog_freq = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_freq_rule, null);
      new AlertDialog.Builder(getActivity())
              .setTitle("Setting Frequency Alert")//設定視窗標題
              .setView(view_dialog_freq)
              // set the action buttons
              .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  int iFreqUpperBound;
                  int iFreqLowerBound;
                  EditText editTextUpperBound = view_dialog_freq.findViewById(R.id.editText_freq_upperBound);
                  EditText editTextLowerBound = view_dialog_freq.findViewById(R.id.editText_freq_lowerBound);
                  editTextUpperBound.setHint(textViewFreqUpperBound.getText().toString());
                  editTextLowerBound.setHint(textViewFreqLowerBound.getText().toString());

                  if (editTextUpperBound.length() > 0 && editTextLowerBound.length() > 0) {
                    iFreqUpperBound = Integer.parseInt(editTextUpperBound.getText().toString());
                    iFreqLowerBound = Integer.parseInt(editTextLowerBound.getText().toString());
                    if (iFreqLowerBound > iFreqUpperBound)
                      Toast.makeText(getActivity(), "Upper bound must bigger than lower bound", Toast.LENGTH_LONG).show();
                    else {
                      textViewFreqLowerBound.setText(Integer.toString(iFreqLowerBound));
                      textViewFreqUpperBound.setText(Integer.toString(iFreqUpperBound));
                      myService.freqLowerBound = iFreqLowerBound;
                      myService.freqUpperBound = iFreqUpperBound;
                    }
                  } else {
                    if (editTextUpperBound.length() > 0) {
                      textViewFreqUpperBound.setText(editTextUpperBound.getText().toString());
                      myService.freqUpperBound = Integer.parseInt(editTextUpperBound.getText().toString());
                      myService.freqLowerBound = 0;
                    }
                    if (editTextLowerBound.length() > 0) {
                      textViewFreqLowerBound.setText(editTextLowerBound.getText().toString());
                      myService.freqLowerBound = Integer.parseInt(editTextLowerBound.getText().toString());
                      myService.freqUpperBound = 100000;
                    }
                  }
                  dialog.cancel();
                }
              })
              .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  dialog.cancel();
                }
              })
              .setNeutralButton("Reset", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                  myService.freqLowerBound = 0;
                  myService.freqUpperBound = 100000;
                  textViewFreqLowerBound.setText("0");
                  textViewFreqUpperBound.setText("100000");
                  dialog.cancel();
                }
              })
              .show();//呈現對話視窗
    }
  };

}
