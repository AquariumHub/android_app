package net.aquariumhub.myapplication;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.borax12.materialdaterangepicker.time.RadialPickerLayout;

import java.util.Calendar;

public class FragmentTabSetting extends DialogFragment {

  ImageButton setting_edit_temp;
  ImageButton setting_edit_bright;
  ImageButton setting_edit_frequency;

  /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
  public interface NoticeDialogListener {
    public void onDialogPositiveClick(DialogFragment dialog);
    public void onDialogNegativeClick(DialogFragment dialog);
  }

  // Use this instance of the interface to deliver action events
  NoticeDialogListener mListener;

  // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
  @Override
  public void onAttach(Activity activity) {
    super.onAttach(activity);
    // Verify that the host activity implements the callback interface
    try {
      // Instantiate the NoticeDialogListener so we can send events to the host
      mListener = (NoticeDialogListener) activity;
    } catch (ClassCastException e) {
      // The activity doesn't implement the interface, throw exception
      throw new ClassCastException(activity.toString()
              + " must implement NoticeDialogListener");
    }
  }

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
