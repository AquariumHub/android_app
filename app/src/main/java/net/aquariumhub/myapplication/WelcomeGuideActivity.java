package net.aquariumhub.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class WelcomeGuideActivity extends Activity {

  final String LOG_TAG = "WelcomeGuideActivity";

  LoginButton loginButton;
  CallbackManager callbackManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.splash);

    TextView tvSkip = (TextView) findViewById(R.id.tv_skip);
    tvSkip.setOnClickListener(tvSkipOnClick);

    callbackManager = CallbackManager.Factory.create();

    loginButton = (LoginButton) findViewById(R.id.login_button);
    loginButton.setReadPermissions("email");
    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override
      public void onSuccess(LoginResult loginResult) {
        try{
          enterMainActivity();
        } catch (Exception e){
          Log.e(LOG_TAG, "enterMainActivity(): ", e);
        }
      }

      @Override
      public void onCancel() {
        enterMainActivity();
      }

      @Override
      public void onError(FacebookException error) {
        Log.e(LOG_TAG, "FacebookException: ", error );
        enterMainActivity();
      }
    });
  }

  @Override
  protected void onPause() {
    super.onPause();
    SharedPreferences activityPreferences = getApplicationContext().getSharedPreferences("app", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = activityPreferences.edit();
    editor.putBoolean("first_open", true);
    editor.apply();
    finish();
  }

  public void enterMainActivity() {
    Intent intent = new Intent(WelcomeGuideActivity.this, MainActivity.class);
    startActivity(intent);

    SharedPreferences activityPreferences = getApplicationContext().getSharedPreferences("app", Context.MODE_PRIVATE);
    SharedPreferences.Editor editor = activityPreferences.edit();
    editor.putBoolean("first_open", true);
    editor.apply();

    finish();
  }

  TextView.OnClickListener tvSkipOnClick = new TextView.OnClickListener() {
    @Override
    public void onClick(View v) {
      enterMainActivity();
    }
  };
}
