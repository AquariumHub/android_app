package net.aquariumhub.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

  /**
   * Tag for looking for error messages in the android device monitor
   */
  private static final String LOG_TAG = "LogDemo";

  private ViewPager mViewPager;
  private List<Fragment> mFragments = new ArrayList<>(); // list of fragments

  /**
   * LinearLayout in the bottom_bar.xml
   */
  private LinearLayout lLayoutTabBottomHub;
  private LinearLayout lLayoutTabBottomStatus;
  private LinearLayout lLayoutTabBottomSetting;
  private LinearLayout lLayoutTabBottomHistory;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //requestWindowFeature(Window.FEATURE_NO_TITLE);
    setContentView(R.layout.activity_main);
    mViewPager = (ViewPager) findViewById(R.id.viewPager);

    initView(); // setup the default resources of view objects

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
    mViewPager.setOffscreenPageLimit(2);

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
}