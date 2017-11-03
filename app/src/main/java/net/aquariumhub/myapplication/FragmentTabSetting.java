package net.aquariumhub.myapplication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;

import java.util.Calendar;

public class FragmentTabSetting extends Fragment {

  ImageButton setting_edit_temp;
  ImageButton setting_edit_bright;
  ImageButton setting_edit_frequency;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.tab_setting, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    setting_edit_temp = getActivity().findViewById(R.id.setting_edit_temp);
    setting_edit_bright = getActivity().findViewById(R.id.setting_edit_bright);
    setting_edit_frequency = getActivity().findViewById(R.id.setting_edit_frequency);

    setting_edit_temp.setOnClickListener(setting_edit_temp_clicked);
    setting_edit_bright.setOnClickListener(setting_edit_bright_clicked);
    setting_edit_frequency.setOnClickListener(setting_edit_frequency_clicked);
  }

  ImageButton.OnClickListener setting_edit_temp_clicked = new ImageButton.OnClickListener() {
    @Override
    public void onClick(View view) {
      //產生視窗物件
      new AlertDialog.Builder(getActivity())
              .setTitle("Setting Temp Alert")//設定視窗標題
              .setView(R.layout.dialog_temp_rule)
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
