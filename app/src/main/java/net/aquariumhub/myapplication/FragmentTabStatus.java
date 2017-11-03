package net.aquariumhub.myapplication;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;
import java.io.UnsupportedEncodingException;

import static android.content.Context.NOTIFICATION_SERVICE;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by michael on 2017/9/9.
 */

public class FragmentTabStatus extends Fragment {

  private static final String LOG_TAG = "FragmentTabStatus";

  private AwsService.MyBinder myBinder;
  private AwsService myService;
  boolean mBounded;

  private Intent intent;
  private static final int NOTIFICATION_ID = 0;
  private NotificationManager notificationManger;
  private Notification notification;

  TextView tvValueTemperature;
  TextView tvValueBrightness;
  TextView tvValueLightFrequency;

  WebView wvGaugeTemperature;
  String URL_GRAPH_TEMP = "http://ec2-13-115-112-36.ap-northeast-1.compute.amazonaws.com/gauge/temperature.php";

  WebView wvGaugeBrightness;
  String URL_GRAPH_BRIGHT = "http://ec2-13-115-112-36.ap-northeast-1.compute.amazonaws.com/gauge/brightness.php";

  WebView wvGaugeFreqency;
  String URL_GRAPH_FREQ = "http://ec2-13-115-112-36.ap-northeast-1.compute.amazonaws.com/gauge/lightFrequency.php";

  ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      //Toast.makeText(getActivity(), "Service is connected", Toast.LENGTH_LONG).show();
      myBinder = (AwsService.MyBinder) service;
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
    return inflater.inflate(R.layout.tab_status, container, false);
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {

    tvValueTemperature = (TextView) view.findViewById(R.id.tv_value_temperature);
    tvValueBrightness = (TextView) view.findViewById(R.id.tv_value_brightness);
    tvValueLightFrequency = (TextView) view.findViewById(R.id.tv_value_lightFrequency);

    tvValueTemperature.setText(getString(R.string.value_temperature, "0"));
    tvValueBrightness.setText(getString(R.string.value_brightness, "0"));
    tvValueLightFrequency.setText(getString(R.string.value_lightFrequency, "0"));

    wvGaugeTemperature = (WebView) getActivity().findViewById(R.id.wv_gauge_temp);
    wvGaugeTemperature.getSettings().setJavaScriptEnabled(true);
    wvGaugeTemperature.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        return motionEvent.getAction() == MotionEvent.ACTION_MOVE;
      }
    });
    wvGaugeTemperature.setVerticalScrollBarEnabled(false);
    wvGaugeTemperature.setHorizontalScrollBarEnabled(false);

    wvGaugeBrightness = (WebView) getActivity().findViewById(R.id.wv_gauge_bright);
    wvGaugeBrightness.getSettings().setJavaScriptEnabled(true);
    wvGaugeBrightness.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        return motionEvent.getAction() == MotionEvent.ACTION_MOVE;
      }
    });
    wvGaugeBrightness.setVerticalScrollBarEnabled(false);
    wvGaugeBrightness.setHorizontalScrollBarEnabled(false);

    wvGaugeFreqency = (WebView) getActivity().findViewById(R.id.wv_gauge_freq);
    wvGaugeFreqency.getSettings().setJavaScriptEnabled(true);
    wvGaugeFreqency.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent) {
        return motionEvent.getAction() == MotionEvent.ACTION_MOVE;
      }
    });
    wvGaugeFreqency.setVerticalScrollBarEnabled(false);
    wvGaugeFreqency.setHorizontalScrollBarEnabled(false);

  }

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);
    if (isVisibleToUser) {
      subscribeToTopic();
    } else {
      // fragment is no longer visible
    }
  }

  boolean isFirstTempNotification = true;
  boolean isFirstBrightNotification = true;
  boolean isFirstFreqNotification = true;
  int countTemp = 0;
  int countBright = 0;
  int countFreq = 0;

  public void subscribeToTopic() {
    Intent myIntent = new Intent(getActivity(), AwsService.class);
    getActivity().bindService(myIntent, serviceConnection, BIND_AUTO_CREATE);

    try {
      wvGaugeTemperature.loadUrl(URL_GRAPH_TEMP + "?lowerBound=" + Integer.toString(myService.tempLowerBound) + "&upperBound=" + Integer.toString(myService.tempUpperBound));
      wvGaugeBrightness.loadUrl(URL_GRAPH_BRIGHT + "?lowerBound=" + Integer.toString(myService.brightLowerBound) + "&upperBound=" + Integer.toString(myService.brightUpperBound));
      wvGaugeFreqency.loadUrl(URL_GRAPH_FREQ + "?lowerBound=" + Integer.toString(myService.freqLowerBound) + "&upperBound=" + Integer.toString(myService.freqUpperBound));

      final String topic = "sensingData";
      if (myService.mqttManager != null)
        myService.mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                new AWSIotMqttNewMessageCallback() {

                  @Override
                  public void onMessageArrived(final String topic, final byte[] data) {

                    countTemp++;
                    countBright++;
                    countFreq++;

                    getActivity().runOnUiThread(new Runnable() {

                      @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                      @Override
                      public void run() {

                        try {
                          String message = new String(data, "UTF-8");
                          JSONObject mJsonObject = new JSONObject(message);

                          Log.d(LOG_TAG, "Message arrived:");
                          Log.d(LOG_TAG, "Topic: " + topic);
                          Log.d(LOG_TAG, "Temperature: " + mJsonObject.getString("temperature") +
                                  "\nBrightness: " + mJsonObject.getString("brightness") +
                                  "\nLightFrequency: " + mJsonObject.getString("lightFrequency") +
                                  "\n---------------------------------");

                          tvValueTemperature.setText(getString(R.string.value_temperature, mJsonObject.getString("temperature")));
                          tvValueBrightness.setText(getString(R.string.value_brightness, mJsonObject.getString("brightness")));
                          tvValueLightFrequency.setText(getString(R.string.value_lightFrequency, mJsonObject.getString("lightFrequency")));

                          float iTempValue = Float.parseFloat(mJsonObject.getString("temperature"));
                          int iBrightValue = Integer.parseInt(mJsonObject.getString("brightness"));
                          int iFreqValue = Integer.parseInt(mJsonObject.getString("lightFrequency"));

                          notificationManger = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

                          intent = new Intent();
                          intent.setClass(getActivity(), HandleNotification.class);
                          intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                  | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                          PendingIntent pendingIntent =
                                  PendingIntent.getActivity(getActivity(), NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                          if (iTempValue < myService.tempLowerBound && (countTemp > 300 || isFirstTempNotification)) {
                            countTemp = 0;
                            isFirstTempNotification = false;
                            notification = new Notification.Builder(getActivity())
                                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                    .setContentTitle("Warning!")
                                    .setContentText("您的 AquariumHub 溫度太低!")
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .build(); // available from API level 11 and onwards
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notificationManger.notify(0, notification);
                          }

                          if (iTempValue > myService.tempUpperBound && (countTemp > 300 || isFirstTempNotification)) {
                            countTemp = 0;
                            isFirstTempNotification = false;
                            notification = new Notification.Builder(getActivity())
                                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                    .setContentTitle("Warning!")
                                    .setContentText("您的 AquariumHub 溫度太高!")
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .build(); // available from API level 11 and onwards
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notificationManger.notify(0, notification);
                          }

                          if (iBrightValue < myService.brightLowerBound && (countBright > 300 || isFirstBrightNotification)) {
                            countBright = 0;
                            isFirstBrightNotification = false;
                            notification = new Notification.Builder(getActivity())
                                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                    .setContentTitle("Warning!")
                                    .setContentText("您的 AquariumHub 亮度太低!")
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .build(); // available from API level 11 and onwards
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notificationManger.notify(0, notification);
                          }

                          if (iBrightValue > myService.brightUpperBound && (countBright > 300 || isFirstBrightNotification)) {
                            countBright = 0;
                            isFirstBrightNotification = false;
                            notification = new Notification.Builder(myService.getApplicationContext())
                                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                    .setContentTitle("Warning!")
                                    .setContentText("您的 AquariumHub 亮度太高!")
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .build(); // available from API level 11 and onwards
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notificationManger.notify(0, notification);
                          }

                          if (iFreqValue < myService.freqLowerBound && (countFreq > 300 || isFirstFreqNotification)) {
                            countFreq = 0;
                            isFirstFreqNotification = false;
                            notification = new Notification.Builder(getActivity())
                                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                    .setContentTitle("Warning!")
                                    .setContentText("您的 AquariumHub 光頻太低!")
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .build(); // available from API level 11 and onwards
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notificationManger.notify(0, notification);
                          }

                          if (iFreqValue > myService.freqUpperBound && (countFreq > 300 || isFirstFreqNotification)) {
                            countFreq = 0;
                            isFirstFreqNotification = false;
                            notification = new Notification.Builder(getActivity())
                                    .setSmallIcon(android.R.drawable.sym_def_app_icon)
                                    .setContentTitle("Warning!")
                                    .setContentText("您的 AquariumHub 光頻太高!")
                                    .setContentIntent(pendingIntent)
                                    .setDefaults(Notification.DEFAULT_ALL)
                                    .build(); // available from API level 11 and onwards
                            notification.flags = Notification.FLAG_AUTO_CANCEL;
                            notificationManger.notify(0, notification);
                          }
                        } catch (Exception e){
                          Log.e(LOG_TAG, "FragmentTabStatus", e);
                        } /*catch (UnsupportedEncodingException e) {
                          Log.e(LOG_TAG, "Message encoding error.", e);
                        } catch (JSONException e) {
                          e.printStackTrace();
                        }*/
                      }
                    });
                  }
                });
    } catch (Exception e) {
      Log.e(LOG_TAG, "Subscription error.", e);
    }
  }

  @Override
  public void onStart() {
    super.onStart();
  }

  @Override
  public void onStop() {
    super.onStop();
    if (mBounded) {
      getActivity().unbindService(serviceConnection);
      mBounded = false;
    }
  }
}
