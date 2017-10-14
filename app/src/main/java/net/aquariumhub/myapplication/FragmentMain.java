package net.aquariumhub.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class FragmentMain extends Fragment implements View.OnClickListener {

  final String TAG = "FragmentMain";

  private ViewPager mViewPager;
  private List<Fragment> mFragments = new ArrayList<>(); // list of fragments

  // LinearLayout in the bottom_bar.xml
  private LinearLayout lLayoutTabBottomHub;
  private LinearLayout lLayoutTabBottomStatus;
  private LinearLayout lLayoutTabBottomSetting;
  private LinearLayout lLayoutTabBottomHistory;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    // Inflate the layout for this fragment
    return inflater.inflate(R.layout.fragment_main, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    mViewPager = view.findViewById(R.id.viewPager);

    initView(); // setup the default resources of view objects

    FragmentPagerAdapter mAdapter = new FragmentPagerAdapter(getFragmentManager()) {
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

      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      // Set picture of imageButton on the bottom bar while page changing
      @Override
      public void onPageSelected(int position) {

        resetTabBtn();

        switch (position) {
          case 0:
            ((ImageButton) lLayoutTabBottomHub.findViewById(R.id.iButton_tab_hub))
                    .setImageResource(R.drawable.ic_hub_pressed);
            ((TextView) lLayoutTabBottomHub.findViewById(R.id.tv_tab_hub))
                    .setTextColor(getResources().getColor(R.color.skyBlue));
            break;
          case 1:
            ((ImageButton) lLayoutTabBottomStatus.findViewById(R.id.iButton_tab_status))
                    .setImageResource(R.drawable.ic_status_pressed);
            ((TextView) lLayoutTabBottomStatus.findViewById(R.id.tv_tab_status))
                    .setTextColor(getResources().getColor(R.color.skyBlue));
            break;
          case 2:
            ((ImageButton) lLayoutTabBottomSetting.findViewById(R.id.iButton_tab_setting))
                    .setImageResource(R.drawable.ic_setting_pressed);
            ((TextView) lLayoutTabBottomSetting.findViewById(R.id.tv_tab_setting))
                    .setTextColor(getResources().getColor(R.color.skyBlue));
            break;
          case 3:
            ((ImageButton) lLayoutTabBottomHistory.findViewById(R.id.iButton_tab_history))
                    .setImageResource(R.drawable.ic_history_pressed);
            ((TextView) lLayoutTabBottomHistory.findViewById(R.id.tv_tab_history))
                    .setTextColor(getResources().getColor(R.color.skyBlue));
            break;
        }
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });
  }

  private void initView() {

    lLayoutTabBottomHub = getView().findViewById(R.id.lLayout_tab_hub);
    lLayoutTabBottomStatus = getView().findViewById(R.id.lLayout_tab_status);
    lLayoutTabBottomSetting = getView().findViewById(R.id.lLayout_tab_setting);
    lLayoutTabBottomHistory = getView().findViewById(R.id.lLayout_tab_history);

    ImageButton iButtonTabHub = getView().findViewById(R.id.iButton_tab_hub);
    ImageButton iButtonTabStatus = getView().findViewById(R.id.iButton_tab_status);
    ImageButton iButtonTabSetting = getView().findViewById(R.id.iButton_tab_setting);
    ImageButton iButtonTabHistory = getView().findViewById(R.id.iButton_tab_history);

    iButtonTabHub.setOnClickListener(this);
    iButtonTabStatus.setOnClickListener(this);
    iButtonTabSetting.setOnClickListener(this);
    iButtonTabHistory.setOnClickListener(this);

    ((ImageButton) lLayoutTabBottomHub.findViewById(R.id.iButton_tab_hub))
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

  // Reset the view of button on bottom bar to default
  protected void resetTabBtn() {
    ((ImageButton) lLayoutTabBottomHub.findViewById(R.id.iButton_tab_hub)).
            setImageResource(R.drawable.ic_hub_normal);
    ((ImageButton) lLayoutTabBottomStatus.findViewById(R.id.iButton_tab_status)).
            setImageResource(R.drawable.ic_status_normal);
    ((ImageButton) lLayoutTabBottomSetting.findViewById(R.id.iButton_tab_setting)).
            setImageResource(R.drawable.ic_setting_normal);
    ((ImageButton) lLayoutTabBottomHistory.findViewById(R.id.iButton_tab_history)).
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

  // Change page by clicking on tab button
  @Override
  public void onClick(View view) {
    switch (view.getId()) {

      case R.id.iButton_tab_hub:
      case R.id.tv_tab_hub:
        mViewPager.setCurrentItem(0, true);
        break;

      case R.id.iButton_tab_status:
      case R.id.tv_tab_status:
        mViewPager.setCurrentItem(1, true);
        break;

      case R.id.iButton_tab_setting:
      case R.id.tv_tab_setting:
        mViewPager.setCurrentItem(2, true);
        break;

      case R.id.iButton_tab_history:
      case R.id.tv_tab_history:
        mViewPager.setCurrentItem(3, true);
        break;

      default:
        break;
    }
  }
}
