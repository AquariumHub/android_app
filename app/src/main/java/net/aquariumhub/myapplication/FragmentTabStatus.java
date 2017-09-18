package net.aquariumhub.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by michael on 2017/9/9.
 */

public class FragmentTabStatus extends Fragment {

  private static final String LOG_TAG = "FragmentTabStatus";

  private AwsService.MyBinder myBinder;
  private AwsService myService;
  boolean mBounded;

  TextView tvHubTemperature;
  TextView tvHubBrightness;
  TextView tvHubLightFrequency;

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

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {

    tvHubTemperature = (TextView) view.findViewById(R.id.tv_hub_temperature);
    tvHubBrightness = (TextView) view.findViewById(R.id.tv_hub_brightness);
    tvHubLightFrequency = (TextView) view.findViewById(R.id.tv_hub_lightFrequency);

    tvHubTemperature.setText(String.format(getString(R.string.hub_temperature), "0"));
    tvHubBrightness.setText(String.format(getString(R.string.hub_brightness), "0"));
    tvHubLightFrequency.setText(String.format(getString(R.string.hub_lightFrequency), "0"));


  }

  @Override
  public void onStart() {
    super.onStart();
    Intent myIntent = new Intent(getActivity(), AwsService.class);
    getActivity().bindService(myIntent, serviceConnection, BIND_AUTO_CREATE);

    try {
      final String topic = "sensingData";
      myService.mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
              new AWSIotMqttNewMessageCallback() {

                @Override
                public void onMessageArrived(final String topic, final byte[] data) {

                  getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                      try {
                        String message = new String(data, "UTF-8");
                        Log.d(LOG_TAG, "Message arrived:");
                        Log.d(LOG_TAG, "   Topic: " + topic);
                        Log.d(LOG_TAG, " Message: " + message);

                        JSONObject mJsonObject = new JSONObject(message);

                        tvHubTemperature.setText(String.format(getString(R.string.hub_temperature), mJsonObject.getString("temperature")));
                        tvHubBrightness.setText(String.format(getString(R.string.hub_brightness), mJsonObject.getString("brightness")));
                        tvHubLightFrequency.setText(String.format(getString(R.string.hub_lightFrequency), mJsonObject.getString("lightFrequency")));

                      } catch (UnsupportedEncodingException e) {
                        Log.e(LOG_TAG, "Message encoding error.", e);
                      } catch (JSONException e) {
                        e.printStackTrace();
                      }
                    }
                  });
                }
              });
    } catch (Exception e) {
      Log.e(LOG_TAG, "Subscription error.", e);
    }
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