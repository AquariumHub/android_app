package net.aquariumhub.myapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.facebook.CallbackManager;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
  /**
   * Tag for looking for error messages in the android device monitor
   */
  private static final String TAG = "LogDemo";

  private ViewPager mViewPager;
  private List<Fragment> mFragments = new ArrayList<>(); // list of fragments

  /**
   * LinearLayout in the bottom_bar.xml
   */
  private LinearLayout lLayoutTabBottomHub;
  private LinearLayout lLayoutTabBottomStatus;
  private LinearLayout lLayoutTabBottomSetting;
  private LinearLayout lLayoutTabBottomHistory;

  /**
   * Facebook instances
   */
  private LoginManager loginManager;
  private CallbackManager callbackManager;
  private ProfileTracker profileTracker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    mViewPager = (ViewPager) findViewById(R.id.viewPager);

    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.addDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

    initView(); // setup the default resources of view objects

    // ImageView socialLogin = (ImageView)  navigationView.getHeaderView(0).findViewById(R.id.social_login);
    ImageView userPhoto = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.user_photo);
    TextView userName = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_name);
    TextView userstatus = (TextView) navigationView.getHeaderView(0).findViewById(R.id.user_status);

    /**
     * Get user profile
     */
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
        userstatus.setText("Facebook");
        // socialLogin.setBackgroundResource(R.drawable.logout_white);
      } catch (Exception e) {
        Log.e(TAG, "Errors occurred while getting user profile: ", e);
      }
    } else {
      userPhoto.setImageResource(R.mipmap.ic_launcher_round);
      userName.setText(R.string.app_name);
      userstatus.setText("Social Website");
    }

    FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
      @Override
      public int getCount() {
        return mFragments.size();
      }

      @Override
      public Fragment getItem(int position) {
        return mFragments.get(position);
      }
    };

    mViewPager.setAdapter(mAdapter);
    // mViewPager.setOffscreenPageLimit(2);
    mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

      private int currentIndex;

      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      /**
       *  Set picture of imageButton on the bottom bar while page changing
       */
      @Override
      public void onPageSelected(int position) {

        resetTabBtn();

        switch (position) {
          case 0:
            ((ImageButton) lLayoutTabBottomHub.findViewById(R.id.iButton_tab_bottom_hub))
                    .setImageResource(R.drawable.ic_hub_pressed);
            ((TextView) lLayoutTabBottomHub.findViewById(R.id.tv_tab_hub))
                    .setTextColor(getResources().getColor(R.color.skyBlue));
            break;
          case 1:
            ((ImageButton) lLayoutTabBottomStatus.findViewById(R.id.iButton_tab_bottom_status))
                    .setImageResource(R.drawable.ic_status_pressed);
            ((TextView) lLayoutTabBottomStatus.findViewById(R.id.tv_tab_status))
                    .setTextColor(getResources().getColor(R.color.skyBlue));
            break;
          case 2:
            ((ImageButton) lLayoutTabBottomSetting.findViewById(R.id.iButton_tab_bottom_setting))
                    .setImageResource(R.drawable.ic_setting_pressed);
            ((TextView) lLayoutTabBottomSetting.findViewById(R.id.tv_tab_setting))
                    .setTextColor(getResources().getColor(R.color.skyBlue));
            break;
          case 3:
            ((ImageButton) lLayoutTabBottomHistory.findViewById(R.id.iButton_tab_bottom_history))
                    .setImageResource(R.drawable.ic_history_pressed);
            ((TextView) lLayoutTabBottomHistory.findViewById(R.id.tv_tab_history))
                    .setTextColor(getResources().getColor(R.color.skyBlue));
            break;
        }

        currentIndex = position;
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  private void initView() {

    lLayoutTabBottomHub = (LinearLayout) findViewById(R.id.lLayout_tab_bottom_hub);
    lLayoutTabBottomStatus = (LinearLayout) findViewById(R.id.lLayout_tab_bottom_status);
    lLayoutTabBottomSetting = (LinearLayout) findViewById(R.id.lLayout_tab_bottom_setting);
    lLayoutTabBottomHistory = (LinearLayout) findViewById(R.id.lLayout_tab_bottom_history);

    ((ImageButton) lLayoutTabBottomHub.findViewById(R.id.iButton_tab_bottom_hub))
            .setImageResource(R.drawable.ic_hub_pressed);
    ((TextView) lLayoutTabBottomHub.findViewById(R.id.tv_tab_hub))
            .setTextColor(getResources().getColor(R.color.skyBlue));

    FragmentTabHub tabHub = new FragmentTabHub();
    FragmentTabStatus tabStatus = new FragmentTabStatus();
    FragmentTabSetting tabSetting = new FragmentTabSetting();
    FragmentTabHistory tabHistory = new FragmentTabHistory();

    mFragments.add(tabHub);
    mFragments.add(tabStatus);
    mFragments.add(tabSetting);
    mFragments.add(tabHistory);
  }

  /**
   * Reset the view of button on bottom bar to default
   */
  protected void resetTabBtn() {
    ((ImageButton) lLayoutTabBottomHub.findViewById(R.id.iButton_tab_bottom_hub)).
            setImageResource(R.drawable.ic_hub_normal);
    ((ImageButton) lLayoutTabBottomStatus.findViewById(R.id.iButton_tab_bottom_status)).
            setImageResource(R.drawable.ic_status_normal);
    ((ImageButton) lLayoutTabBottomSetting.findViewById(R.id.iButton_tab_bottom_setting)).
            setImageResource(R.drawable.ic_setting_normal);
    ((ImageButton) lLayoutTabBottomHistory.findViewById(R.id.iButton_tab_bottom_history)).
            setImageResource(R.drawable.ic_history_normal);

    ((TextView) lLayoutTabBottomHub.findViewById(R.id.tv_tab_hub))
            .setTextColor(getResources().getColor(R.color.normal));
    ((TextView) lLayoutTabBottomStatus.findViewById(R.id.tv_tab_status))
            .setTextColor(getResources().getColor(R.color.normal));
    ((TextView) lLayoutTabBottomSetting.findViewById(R.id.tv_tab_setting))
            .setTextColor(getResources().getColor(R.color.normal));
    ((TextView) lLayoutTabBottomHistory.findViewById(R.id.tv_tab_history))
            .setTextColor(getResources().getColor(R.color.normal));
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

  /**
   * Change page by pressing tab button
   */
  public void btnHubPressed(View view) {
    mViewPager.setCurrentItem(0, true);
  }

  public void btnStatusPressed(View view) {
    mViewPager.setCurrentItem(1, true);
  }

  public void btnSettingPressed(View view) {
    mViewPager.setCurrentItem(2, true);
  }

  public void btnHistoryPressed(View view) {
    mViewPager.setCurrentItem(3, true);
  }


  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
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
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @SuppressWarnings("StatementWithEmptyBody")
  @Override
  public boolean onNavigationItemSelected(@NonNull MenuItem item) {
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    if (id == R.id.nav_camera) {
      // Handle the camera action
    } else if (id == R.id.nav_gallery) {

    } else if (id == R.id.nav_slideshow) {

    } else if (id == R.id.nav_manage) {

    } else if (id == R.id.nav_share) {

    } else if (id == R.id.nav_send) {

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
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