package net.aquariumhub.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SocialLogin extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.social_login);

    android.support.v7.app.ActionBar actionBar = getSupportActionBar();
    actionBar.setTitle("Social Login");
  }
}
