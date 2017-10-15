package net.aquariumhub.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Printer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

  // Tag for looking for error messages in the android device monitor
  private static final String TAG = "MainActivity";

  // Facebook instances
  private LoginManager loginManager;
  private CallbackManager callbackManager;
  private ProfileTracker profileTracker;

  // ImageView socialLogin = navigationView.getHeaderView(0).findViewById(R.id.social_login);
  ImageView userPhoto;
  TextView userName;
  TextView userStatus;

  FragmentManager fragmentManager;
  FragmentMain fragmentMain;
  FragmentSelectDevice fragmentSelectDevice;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    fragmentMain = new FragmentMain();
    fragmentTransaction.add(R.id.container_main, fragmentMain).commit();

    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    // ImageView socialLogin = (ImageView)  navigationView.getHeaderView(0).findViewById(R.id.social_login);
    userPhoto = navigationView.getHeaderView(0).findViewById(R.id.user_photo);
    userName = navigationView.getHeaderView(0).findViewById(R.id.user_name);
    userStatus = navigationView.getHeaderView(0).findViewById(R.id.user_status);
  }

  @Override
  public void onStart() {
    super.onStart();

    // Get FB user profile
    if (Profile.getCurrentProfile() != null) {
      Profile profile = Profile.getCurrentProfile();
      // get the user profile picture
      Uri uri_userPhoto = profile.getProfilePictureUri(300, 300);
      String id = profile.getId();
      String name = profile.getName();
      Log.d(TAG, "Facebook userPhoto: " + uri_userPhoto);
      Log.d(TAG, "Facebook id: " + id);
      Log.d(TAG, "Facebook name: " + name);
      try {
        Glide.with(this).load(uri_userPhoto)
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(userPhoto);
        userName.setText(name);
        userStatus.setText("Facebook");
        // socialLogin.setBackgroundResource(R.drawable.logout_white);
      } catch (Exception e) {
        Log.e(TAG, "Errors occurred while getting user profile: ", e);
      }
    } else {
      userPhoto.setImageResource(R.mipmap.ic_launcher_round);
      userName.setText(R.string.app_name);
      userStatus.setText("Social Website");
    }

  }

  /**
   * Press title button to show the popup menu on the title bar
   */
  public void showPopup(View v) {
    PopupMenu popup = new PopupMenu(this, v);
    MenuInflater inflater = popup.getMenuInflater();
    inflater.inflate(R.menu.menu_top_right, popup.getMenu());
    popup.show();
  }


  @Override
  public void onBackPressed() {
    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      //startActivity(new Intent(getApplicationContext(), ActivitySettings.class));
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private int currentPage;

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    Log.d(TAG, "onNavigationItemSelected");

    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    hideFragments(fragmentTransaction);

    switch (item.getItemId()) {
      case R.id.nav_aquarium:
        Log.d(TAG, "nav_aquarium");
        if (fragmentMain == null) {
          fragmentMain = new FragmentMain();
          fragmentTransaction.add(R.id.container_main, fragmentMain);
        } else {
          fragmentTransaction.show(fragmentMain);
        }
        break;

      case R.id.nav_select_device:
        Log.d(TAG, "nav_select_device");
        if (fragmentSelectDevice == null) {
          fragmentSelectDevice = new FragmentSelectDevice();
          fragmentTransaction.add(R.id.container_main, fragmentSelectDevice);
        } else {
          fragmentTransaction.show(fragmentSelectDevice);
        }
        break;

      case R.id.nav_manage:
        break;

      case R.id.nav_share:
        break;

      case R.id.nav_send:
        break;

      default:
        break;
    }

    fragmentTransaction.commit();

    DrawerLayout drawer = findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  private void hideFragments(FragmentTransaction fragmentTransaction) {

    if (fragmentMain != null) {
      fragmentTransaction.hide(fragmentMain);
    }
    if (fragmentSelectDevice != null) {
      fragmentTransaction.hide(fragmentSelectDevice);
    }

  }

  public void enterSocialLoginActivity(View v) {
    Intent intent = new Intent(this, SocialLogin.class);
    startActivity(intent);
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // if you don't add following block,
    // your registered `FacebookCallback` won't be called
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }
}