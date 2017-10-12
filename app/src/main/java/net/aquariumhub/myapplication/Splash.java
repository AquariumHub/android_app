package net.aquariumhub.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // if it is the first time the app is installed
    boolean isFirstOpen;
    SharedPreferences preferences = this.getSharedPreferences("app", Context.MODE_PRIVATE);
    isFirstOpen = preferences.getBoolean("first_open", false);

    if (!isFirstOpen) {
      Intent intent = new Intent(this, WelcomeGuideActivity.class);
      startActivity(intent);
      finish();
      return;
    }

    setContentView(R.layout.welcome);

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        enterHomeActivity();
      }
    }, 1500);
  }

  private void enterHomeActivity() {
    Intent intent = new Intent(this, AwsService.class);
    startService(intent);
    intent = new Intent(this, MainActivity.class);
    startActivity(intent);
    finish();
  }

}
