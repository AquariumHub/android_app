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
import android.widget.SeekBar;
import android.widget.TextView;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by michael on 2017/9/9.
 */

public class FragmentTabHub extends Fragment {

    /**
     * Tag for looking for error messages in the android device monitor
     */
    final static String LOG_TAG = "FragmentTabHub";

    private AwsService.MyBinder myBinder;
    private AwsService myService;
    boolean mBounded;

    TextView tvAp700Intensity;
    TextView tvAp700Color;
    TextView tvA360Intensity;
    TextView tvA360Color;

    SeekBar seekbarAP700Intensity;
    SeekBar seekbarAP700Color;
    SeekBar seekbarA360Intensity;
    SeekBar seekbarA360Color;

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
                Log.e(LOG_TAG, "something wrong with seekbar of ap700 intensity.", e);
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
                Log.e(LOG_TAG, "something wrong with seek bar of ap700 color.", e);
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
                Log.e(LOG_TAG, "something wrong with seek bar of a360 intensity.", e);
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
                Log.e(LOG_TAG, "something wrong with seek bar of a360 color.", e);
            }
        }
    };
}
