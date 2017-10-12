package net.aquariumhub.myapplication;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

import static android.content.Context.BIND_AUTO_CREATE;

public class FragmentTabSetting extends Fragment {
  /**
   * Tag for looking for error messages in the android device monitor
   */
  private static final String TAG = "FragmentTabSetting";

  private AwsService myService;
  boolean mBounded;

  EditText txtSubscribe;
  EditText txtTopic;
  EditText txtMessage;

  TextView tvLastMessage;
  TextView tvClientId;
  TextView tvStatus;

  Button btnConnect;
  Button btnSubscribe;
  Button btnPublish;
  Button btnDisconnect;

  String clientId;

  ServiceConnection serviceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
      Log.d(TAG, "onServiceConnected");
      AwsService.MyBinder myBinder = (AwsService.MyBinder) service;
      myService = myBinder.getAwsServiceInstance();
      mBounded = true;
      tvStatus.setText(myService.currentStatus);
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
      Log.d(TAG, "onServiceDisconnected");
      mBounded = false;
      myService = null;
    }
  };

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

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  private TextView mTextMessage;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.tab_setting, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {

    mTextMessage = (TextView) view.findViewById(R.id.message);

    txtSubscribe = (EditText) view.findViewById(R.id.txtSubcribe);
    txtTopic = (EditText) view.findViewById(R.id.txtTopic);
    txtMessage = (EditText) view.findViewById(R.id.txtMessage);

    tvLastMessage = (TextView) view.findViewById(R.id.tvLastMessage);
    tvClientId = (TextView) view.findViewById(R.id.tvClientId);
    tvStatus = (TextView) view.findViewById(R.id.tvStatus);

    btnConnect = (Button) view.findViewById(R.id.btnConnect);
    btnConnect.setOnClickListener(connectClick);

    btnSubscribe = (Button) view.findViewById(R.id.btnSubscribe);
    btnSubscribe.setOnClickListener(subscribeClick);

    btnPublish = (Button) view.findViewById(R.id.btnPublish);
    btnPublish.setOnClickListener(publishClick);

    btnDisconnect = (Button) view.findViewById(R.id.btnDisconnect);
    btnDisconnect.setOnClickListener(disconnectClick);

    clientId = UUID.randomUUID().toString();
    tvClientId.setText(clientId);
  }

  View.OnClickListener connectClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {

      if (!myService.awsConnectionAvaliabe) {
        Toast.makeText(getActivity(),
                "server is not ready, please check your setting or try it later"
                , Toast.LENGTH_LONG).show();
        return;
      }

      myService.setResponseStatus(getActivity(), tvStatus);
      myService.connect();

      Log.d(TAG, "clientId = " + myService.clientId);

    }
  };

  View.OnClickListener subscribeClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {

      final String topic = txtSubscribe.getText().toString();

      Log.d(TAG, "topic = " + topic);

      try {
        myService.mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
                new AWSIotMqttNewMessageCallback() {
                  @Override
                  public void onMessageArrived(final String topic, final byte[] data) {
                    getActivity().runOnUiThread(new Runnable() {
                      @Override
                      public void run() {
                        try {
                          String message = new String(data, "UTF-8");
                          Log.d(TAG, "Message arrived:");
                          Log.d(TAG, "   Topic: " + topic);
                          Log.d(TAG, " Message: " + message);

                          tvLastMessage.setText(message);

                        } catch (UnsupportedEncodingException e) {
                          Log.e(TAG, "Message encoding error.", e);
                        }
                      }
                    });
                  }
                });
      } catch (Exception e) {
        Log.e(TAG, "Subscription error.", e);
      }
    }
  };

  View.OnClickListener publishClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      final String topic = txtTopic.getText().toString();
      final String msg = txtMessage.getText().toString();
      try {
        myService.mqttManager.publishString(msg, topic, AWSIotMqttQos.QOS0);
      } catch (Exception e) {
        Log.e(TAG, "Publish error.", e);
      }
    }
  };

  View.OnClickListener disconnectClick = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
      try {
        myService.mqttManager.disconnect();
      } catch (Exception e) {
        Log.e(TAG, "Disconnect error.", e);
      }
    }
  };
}
