package net.aquariumhub.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.net.URI;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import static android.content.Context.BIND_AUTO_CREATE;

public class FragmentTabHub extends Fragment {

  /**
   * Tag for looking for error messages in the android device monitor
   */
  final static String TAG = "FragmentTabHub";

  private AwsService myService;
  boolean mBounded;

  TextView tvAp700Intensity;
  TextView tvAp700Color;
  TextView tvA360Intensity;
  TextView tvA360Color;

  // final String URL_VIDEO = "http://mylinkit.local:8080/?action=stream";
  // final String URL_VIDEO = "http://13.115.112.36:4443/?action=stream";
  // final String URL_VIDEO = "http://aquarium-hub.tunnel.qydev.com/?action=stream";
  final String URL_VIDEO = "http://aquariumhub.free.ngrok.cc/?action=stream";
  MjpegView mMjpegView;
  // boolean flag_liveStream = true;

  SeekBar seekbarAP700Intensity;
  SeekBar seekbarAP700Color;
  SeekBar seekbarA360Intensity;
  SeekBar seekbarA360Color;

  Switch aSwitchLiveStream;
  ImageView imageViewLiveStream;

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
    return inflater.inflate(R.layout.tab_hub, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {

    seekbarAP700Intensity = (SeekBar) getActivity().findViewById(R.id.bar_ap700_intensity);
    seekbarAP700Intensity.setOnSeekBarChangeListener(seekbarAP700IntensityChange);
    seekbarAP700Color = (SeekBar) getActivity().findViewById(R.id.bar_ap700_color);
    seekbarAP700Color.setOnSeekBarChangeListener(seekbarAP700ColorChange);

    seekbarA360Intensity = (SeekBar) getActivity().findViewById(R.id.bar_a360_intensity);
    seekbarA360Intensity.setOnSeekBarChangeListener(seekbarA360IntensityChange);
    seekbarA360Color = (SeekBar) getActivity().findViewById(R.id.bar_a360_color);
    seekbarA360Color.setOnSeekBarChangeListener(seekbarA360ColorChange);

    tvAp700Intensity = (TextView) getActivity().findViewById(R.id.title_ap700);
    tvAp700Color = (TextView) getActivity().findViewById(R.id.title_ap700);
    tvA360Intensity = (TextView) getActivity().findViewById(R.id.title_a360);
    tvA360Color = (TextView) getActivity().findViewById(R.id.title_a360);

    aSwitchLiveStream = (Switch) getActivity().findViewById(R.id.switch_liveStream);
    aSwitchLiveStream.setOnCheckedChangeListener(switchLiveStreamOnClick);

    imageViewLiveStream = (ImageView) getActivity().findViewById(R.id.mjpeg_imageView);

    mMjpegView = (MjpegView) getActivity().findViewById(R.id.mjpeg_view);
    aSwitchLiveStream.setChecked(false);
    new DoRead().execute(URL_VIDEO);
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
    mMjpegView.stopPlayback();
    if (mBounded) {
      getActivity().unbindService(serviceConnection);
      mBounded = false;
    }
  }

  private boolean isVisible = false;

  @Override
  public void setUserVisibleHint(boolean isVisibleToUser) {
    super.setUserVisibleHint(isVisibleToUser);

    if (isVisibleToUser) {
      isVisible = true;
      new DoRead().execute(URL_VIDEO);
    } else {
      isVisible = false;
      new DoRead().execute(URL_VIDEO);
    }
  }

  public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {

    private int responseStatusCode;

    protected MjpegInputStream doInBackground(String... url) {
      //TODO: if camera has authentication deal with it and don't just not work
      HttpResponse res;
      DefaultHttpClient httpclient = new DefaultHttpClient();
      Log.d(TAG, "1. Sending http request");
      try {
        res = httpclient.execute(new HttpGet(URI.create(url[0])));
        Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
        responseStatusCode = res.getStatusLine().getStatusCode();
        if (res.getStatusLine().getStatusCode() == 401) {
          //You must turn off camera User Access Control before this will work
          return null;
        }
        return new MjpegInputStream(res.getEntity().getContent());
      } catch (ClientProtocolException e) {
        e.printStackTrace();
        Log.d(TAG, "Request failed-ClientProtocolException", e);
        //Error connecting to camera
      } catch (IOException e) {
        e.printStackTrace();
        Log.d(TAG, "Request failed-IOException", e);
        //Error connecting to camera
      }
      return null;
    }

    @Override
    protected void onPostExecute(MjpegInputStream result) {
      mMjpegView.setSource(result);
      mMjpegView.setDisplayMode(MjpegView.SIZE_BEST_FIT);
      mMjpegView.showFps(true);

      if (!aSwitchLiveStream.isChecked() || !isVisible) {
        Log.d(TAG, "switch of live stream is not checked");
        mMjpegView.stopPlayback();
      } else {
        Log.d(TAG, "switch of live stream is checked");
      }
      if (responseStatusCode == 404) {
        imageViewLiveStream.setVisibility(View.VISIBLE);
      } else {
        imageViewLiveStream.setVisibility(View.GONE);
      }
    }
  }

  Switch.OnCheckedChangeListener switchLiveStreamOnClick = new Switch.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
      if (isChecked) {
        mMjpegView.startPlayback();
      } else {
        mMjpegView.stopPlayback();
      }
    }
  };

  SeekBar.OnSeekBarChangeListener seekbarAP700IntensityChange = new SeekBar.OnSeekBarChangeListener() {
    int valueOfProgress;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      valueOfProgress = progress;
      tvAp700Intensity.setText(String.format(getString(R.string.ap700_intensity), progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      final String TOPIC_SHADOW_UPDATE = "$aws/things/AquariumHub/shadow/update";
      try {
        //tvAp700Intensity.setText("AP700: " + "亮度:" + String.valueOf(valueOfProgress));
        myService.mqttManager.publishString("{\"state\":{\"desired\":{\"AP700\":{\"intensity\":" + String.valueOf(valueOfProgress) + "}}}}", TOPIC_SHADOW_UPDATE, AWSIotMqttQos.QOS0);
      } catch (Exception e) {
        Log.e(TAG, "something wrong with seekbar of ap700 intensity.", e);
      }
    }
  };

  SeekBar.OnSeekBarChangeListener seekbarAP700ColorChange = new SeekBar.OnSeekBarChangeListener() {
    int valueOfProgress;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      valueOfProgress = progress;
      tvAp700Color.setText(String.format(getString(R.string.ap700_color), progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      final String TOPIC_SHADOW_UPDATE = "$aws/things/AquariumHub/shadow/update";
      try {
        //tvAp700Color.setText("AP700: " + "顏色:" + String.valueOf(valueOfProgress));
        myService.mqttManager.publishString("{\"state\":{\"desired\":{\"AP700\":{\"color\":" + String.valueOf(valueOfProgress) + "}}}}", TOPIC_SHADOW_UPDATE, AWSIotMqttQos.QOS0);
      } catch (Exception e) {
        Log.e(TAG, "something wrong with seek bar of ap700 color.", e);
      }
    }
  };

  SeekBar.OnSeekBarChangeListener seekbarA360IntensityChange = new SeekBar.OnSeekBarChangeListener() {
    int valueOfProgress;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      valueOfProgress = progress;
      tvA360Intensity.setText(String.format(getString(R.string.a360_intensity), progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      final String TOPIC_SHADOW_UPDATE = "$aws/things/AquariumHub/shadow/update";
      try {
        //tvA360Intensity.setText("A360: " + "亮度:" + String.valueOf(valueOfProgress));
        myService.mqttManager.publishString("{\"state\":{\"desired\":{\"A360\":{\"intensity\":" + String.valueOf(valueOfProgress) + "}}}}", TOPIC_SHADOW_UPDATE, AWSIotMqttQos.QOS0);
      } catch (Exception e) {
        Log.e(TAG, "something wrong with seek bar of a360 intensity.", e);
      }
    }
  };


  SeekBar.OnSeekBarChangeListener seekbarA360ColorChange = new SeekBar.OnSeekBarChangeListener() {
    int valueOfProgress;

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
      valueOfProgress = progress;
      tvA360Color.setText(String.format(getString(R.string.a360_color), progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
      final String TOPIC_SHADOW_UPDATE = "$aws/things/AquariumHub/shadow/update";
      try {
        //tvA360Color.setText("A360: " + "顏色:" + String.valueOf(valueOfProgress));
        myService.mqttManager.publishString("{\"state\":{\"desired\":{\"A360\":{\"color\":" + String.valueOf(valueOfProgress) + "}}}}", TOPIC_SHADOW_UPDATE, AWSIotMqttQos.QOS0);
      } catch (Exception e) {
        Log.e(TAG, "something wrong with seek bar of a360 color.", e);
      }
    }
  };
}
