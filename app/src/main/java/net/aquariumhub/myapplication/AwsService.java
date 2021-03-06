package net.aquariumhub.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.TextView;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.UUID;

public class AwsService extends Service {
  public int tempUpperBound = 100;
  public int tempLowerBound = 0;
  public int brightUpperBound = 1000;
  public int brightLowerBound = 0;
  public int freqUpperBound = 100000;
  public int freqLowerBound = 0;

  private Intent intent;
  private static final int NOTIFICATION_ID = 0;
  private NotificationManager notificationManger;
  private Notification notification;

  /**
   * Tag for looking for error messages in the android device monitor
   */
  public static final String TAG = "AwsService";
  static final String LOG_TAG = AwsService.class.getCanonicalName();

  // --- Constants to modify per your configuration ---

  // IoT endpoint
  // AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com
  private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a2bdrinkbnov3t.iot.ap-northeast-1.amazonaws.com";
  // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
  // AWS IoT permissions.
  private static final String COGNITO_POOL_ID = "ap-northeast-1:c3b79e39-a8cf-44df-872a-d11c73653855";
  // Name of the AWS IoT policy to attach to a newly created certificate
  private static final String AWS_IOT_POLICY_NAME = "AttachPrincipalPolicy";

  // Region of AWS IoT
  private static final Regions MY_REGION = Regions.AP_NORTHEAST_1;
  // Filename of KeyStore file on the filesystem
  private static final String KEYSTORE_NAME = "iot_keystore";
  // Password for the private key in the KeyStore
  private static final String KEYSTORE_PASSWORD = "password";
  // Certificate and key aliases in the KeyStore
  private static final String CERTIFICATE_ID = "default";

  AWSIotClient mIotAndroidClient;
  AWSIotMqttManager mqttManager;
  String clientId;
  String keystorePath;
  String keystoreName;
  String keystorePassword;

  KeyStore clientKeyStore = null;
  String certificateId;

  CognitoCachingCredentialsProvider credentialsProvider;

  boolean awsConnectionAvaliabe = false;

  private MyBinder mBinder = new MyBinder();

  @Override
  public void onCreate() {
    super.onCreate();
    Log.d(TAG, "onCreate()");

    // MQTT client IDs are required to be unique per AWS IoT account.
    // This UUID is "practically unique" but does not _guarantee_
    // uniqueness.
    clientId = UUID.randomUUID().toString();

    // Initialize the AWS Cognito credentials provider
    credentialsProvider = new CognitoCachingCredentialsProvider(
            getApplicationContext(), // context
            COGNITO_POOL_ID, // Identity Pool ID
            MY_REGION // Region
    );

    Region region = Region.getRegion(MY_REGION);

    mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

    // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
    // MQTT pings every 10 seconds.
    mqttManager.setKeepAlive(10);

    // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
    // AWS IoT will publish this message to alert other clients.
    AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
            "Android client lost connection", AWSIotMqttQos.QOS0);
    mqttManager.setMqttLastWillAndTestament(lwt);

    // IoT Client (for creation of certificate if needed)
    mIotAndroidClient = new AWSIotClient(credentialsProvider);
    mIotAndroidClient.setRegion(region);

    keystorePath = getFilesDir().getPath();
    keystoreName = KEYSTORE_NAME;
    keystorePassword = KEYSTORE_PASSWORD;
    certificateId = CERTIFICATE_ID;

    // To load cert/key from keystore on filesystem
    try {
      if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
        if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                keystoreName, keystorePassword)) {
          Log.i(LOG_TAG, "Certificate " + certificateId
                  + " found in keystore - using for MQTT.");
          // load keystore from file into memory to pass on connection
          clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                  keystorePath, keystoreName, keystorePassword);
          awsConnectionAvaliabe = true;
        } else {
          Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
        }
      } else {
        Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
      }
    } catch (Exception e) {
      Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
    }

    if (clientKeyStore == null) {
      Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

      new Thread(new Runnable() {
        @Override
        public void run() {
          try {
            // Create a new private key and certificate. This call
            // creates both on the server and returns them to the
            // device.
            CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                    new CreateKeysAndCertificateRequest();
            createKeysAndCertificateRequest.setSetAsActive(true);

            final CreateKeysAndCertificateResult createKeysAndCertificateResult;
            createKeysAndCertificateResult =
                    mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);

            Log.i(LOG_TAG,
                    "Cert ID: " +
                            createKeysAndCertificateResult.getCertificateId() +
                            " created.");

            // store in keystore for use in MQTT client
            // saved as alias "default" so a new certificate isn't
            // generated each run of this application
            AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                    createKeysAndCertificateResult.getCertificatePem(),
                    createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                    keystorePath, keystoreName, keystorePassword);

            // load keystore from file into memory to pass on
            // connection
            clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                    keystorePath, keystoreName, keystorePassword);

            // Attach a policy to the newly created certificate.
            // This flow assumes the policy was already created in
            // AWS IoT and we are now just attaching it to the
            // certificate.
            AttachPrincipalPolicyRequest policyAttachRequest =
                    new AttachPrincipalPolicyRequest();
            policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
            policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                    .getCertificateArn());
            mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);

            awsConnectionAvaliabe = true;

          } catch (Exception e) {
            Log.e(LOG_TAG,
                    "Exception occurred when generating new private key and certificate.",
                    e);
          }
        }
      }).start();
    }
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {
    Log.d(TAG, "onStartCommand() executed");
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public void onDestroy() {
    Log.d(TAG, "onDestroy()");
    super.onDestroy();
  }

  @Override
  public IBinder onBind(Intent intent) {
    return mBinder;
  }

  class MyBinder extends Binder {

    AwsService getAwsServiceInstance() {
      return AwsService.this;
    }
  }

  private Activity activity = null;
  private TextView tvStatus = null;
  public String currentStatus = "disconnected";

  public void setResponseStatus(Activity activity, TextView tvStatus) {
    this.activity = activity;
    this.tvStatus = tvStatus;
  }

  public void connect() {

    try {
      mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
        @Override
        public void onStatusChanged(final AWSIotMqttClientStatus status,
                                    final Throwable throwable) {
          Log.d(LOG_TAG, "Status = " + String.valueOf(status));

          if ((activity != null && tvStatus != null) && currentStatus != null)
            activity.runOnUiThread(new Runnable() {
              @Override
              public void run() {
                if (status == AWSIotMqttClientStatus.Connecting) {
                  currentStatus = getString(R.string.connecting);
                  tvStatus.setText(getString(R.string.connecting));
                } else if (status == AWSIotMqttClientStatus.Connected) {
                  currentStatus = getString(R.string.connected);
                  tvStatus.setText(getString(R.string.connected));
                  //subscribeToTopic();
                } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                  if (throwable != null) {
                    Log.e(LOG_TAG, "Connection error.", throwable);
                  }
                  currentStatus = getString(R.string.reconnecting);
                  tvStatus.setText(getString(R.string.reconnecting));
                } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                  if (throwable != null) {
                    Log.e(LOG_TAG, "Connection error.", throwable);
                  }
                  currentStatus = getString(R.string.disconnected);
                  tvStatus.setText(getString(R.string.disconnected));
                } else {
                  currentStatus = getString(R.string.disconnected);
                  tvStatus.setText(getString(R.string.disconnected));
                }
              }
            });
        }
      });
    } catch (final Exception e) {
      Log.e(LOG_TAG, "Connection error.", e);
      currentStatus = getString(R.string.error_message, e.getMessage());
      tvStatus.setText(getString(R.string.error_message, e.getMessage()));
    }
  }

  boolean isFirstTempNotification = true;
  boolean isFirstBrightNotification = true;
  boolean isFirstFreqNotification = true;
  int countTemp = 0;
  int countBright = 0;
  int countFreq = 0;

  public final String CHANNEL_ID_TEMPERATURE = "channelId_temperature";
  public final String CHANNEL_ID_BRIGHTNESS = "channelId_brightness";
  public final String CHANNEL_ID_LIGHTFREQUENCY = "channelId_lightFrequency";

  public void subscribeToTopic() {
    try {
      final String topic = "sensingData";
      mqttManager.subscribeToTopic(topic, AWSIotMqttQos.QOS0,
              new AWSIotMqttNewMessageCallback() {
                @TargetApi(Build.VERSION_CODES.O)
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                @Override
                public void onMessageArrived(final String topic, final byte[] data) {
                  countTemp++;
                  countBright++;
                  countFreq++;
                  try {
                    String message = new String(data, "UTF-8");
                    JSONObject mJsonObject = new JSONObject(message);

                    Log.d(LOG_TAG, "Message arrived:");
                    Log.d(LOG_TAG, "Topic: " + topic);
                    Log.d(LOG_TAG, "Temperature: " + mJsonObject.getString("temperature") +
                            "\nBrightness: " + mJsonObject.getString("brightness") +
                            "\nLightFrequency: " + mJsonObject.getString("lightFrequency") +
                            "\n---------------------------------");

                    float iTempValue = Float.parseFloat(mJsonObject.getString("temperature"));
                    int iBrightValue = Integer.parseInt(mJsonObject.getString("brightness"));
                    int iFreqValue = Integer.parseInt(mJsonObject.getString("lightFrequency"));

                    notificationManger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                    intent = new Intent();
                    intent.setClass(getApplicationContext(), HandleNotification.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    PendingIntent pendingIntent =
                            PendingIntent.getActivity(getApplicationContext(), NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                    if ((iTempValue < tempLowerBound && countTemp > 300) || isFirstTempNotification) {
                      countTemp = 0;
                      isFirstTempNotification = false;
                      notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_TEMPERATURE)
                              .setSmallIcon(android.R.drawable.sym_def_app_icon)
                              .setContentTitle("Warning!")
                              .setContentText("您的 AquariumHub 溫度太低!")
                              .setContentIntent(pendingIntent)
                              .setDefaults(Notification.DEFAULT_ALL)
                              .build(); // available from API level 11 and onwards
                      notification.flags = Notification.FLAG_AUTO_CANCEL;
                      notificationManger.notify(0, notification);
                    }

                    if ((iTempValue > tempUpperBound && countTemp > 300) || isFirstTempNotification) {
                      countTemp = 0;
                      isFirstTempNotification = false;
                      notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_TEMPERATURE)
                              .setSmallIcon(android.R.drawable.sym_def_app_icon)
                              .setContentTitle("Warning!")
                              .setContentText("您的 AquariumHub 溫度太高!")
                              .setContentIntent(pendingIntent)
                              .setDefaults(Notification.DEFAULT_ALL)
                              .build(); // available from API level 11 and onwards
                      notification.flags = Notification.FLAG_AUTO_CANCEL;
                      notificationManger.notify(0, notification);
                    }

                    if ((iBrightValue < brightLowerBound && countBright > 300) || isFirstBrightNotification) {
                      countBright = 0;
                      isFirstBrightNotification = false;
                      notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_BRIGHTNESS)
                              .setSmallIcon(android.R.drawable.sym_def_app_icon)
                              .setContentTitle("Warning!")
                              .setContentText("您的 AquariumHub 亮度太低!")
                              .setContentIntent(pendingIntent)
                              .setDefaults(Notification.DEFAULT_ALL)
                              .build(); // available from API level 11 and onwards
                      notification.flags = Notification.FLAG_AUTO_CANCEL;
                      notificationManger.notify(0, notification);
                    }

                    if ((iBrightValue > brightUpperBound && countBright > 300) || isFirstBrightNotification) {
                      countBright = 0;
                      isFirstBrightNotification = false;
                      notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_BRIGHTNESS)
                              .setSmallIcon(android.R.drawable.sym_def_app_icon)
                              .setContentTitle("Warning!")
                              .setContentText("您的 AquariumHub 亮度太高!")
                              .setContentIntent(pendingIntent)
                              .setDefaults(Notification.DEFAULT_ALL)
                              .build(); // available from API level 11 and onwards
                      notification.flags = Notification.FLAG_AUTO_CANCEL;
                      notificationManger.notify(0, notification);
                    }

                    if ((iFreqValue < freqLowerBound && countFreq > 300) || isFirstFreqNotification) {
                      countFreq = 0;
                      isFirstFreqNotification = false;
                      notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_LIGHTFREQUENCY)
                              .setSmallIcon(android.R.drawable.sym_def_app_icon)
                              .setContentTitle("Warning!")
                              .setContentText("您的 AquariumHub 光頻太低!")
                              .setContentIntent(pendingIntent)
                              .setDefaults(Notification.DEFAULT_ALL)
                              .build(); // available from API level 11 and onwards
                      notification.flags = Notification.FLAG_AUTO_CANCEL;
                      notificationManger.notify(0, notification);
                    }

                    if ((iFreqValue > freqUpperBound && countFreq > 300) || isFirstFreqNotification) {
                      countFreq = 0;
                      isFirstFreqNotification = false;
                      notification = new Notification.Builder(getApplicationContext(), CHANNEL_ID_LIGHTFREQUENCY)
                              .setSmallIcon(android.R.drawable.sym_def_app_icon)
                              .setContentTitle("Warning!")
                              .setContentText("您的 AquariumHub 光頻太高!")
                              .setContentIntent(pendingIntent)
                              .setDefaults(Notification.DEFAULT_ALL)
                              .build(); // available from API level 11 and onwards
                      notification.flags = Notification.FLAG_AUTO_CANCEL;
                      notificationManger.notify(0, notification);
                    }
                  } catch (UnsupportedEncodingException e) {
                    Log.e(LOG_TAG, "Message encoding error.", e);
                  } catch (JSONException e) {
                    e.printStackTrace();
                  }
                }
              });
    } catch (Exception e) {
      Log.e(LOG_TAG, "AwsService", e);
    }
  }
}
