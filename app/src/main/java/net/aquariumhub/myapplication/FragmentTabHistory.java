package net.aquariumhub.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;

public class FragmentTabHistory extends Fragment {

  final String TAG = "FragmentTabHistory";

  private Intent intent;
  private static final int NOTIFICATION_ID = 0;
  private NotificationManager notificationManger;
  private Notification notification;

  /**
   * Facebook instances
   */
  private LoginManager loginManager;
  private CallbackManager callbackManager;
  private ProfileTracker profileTracker;
  ShareDialog shareDialog;

  Bitmap image;

  Button btnNotification;
  ImageView imageView;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.tab_history, container, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    imageView = getActivity().findViewById(R.id.imageView);
    btnNotification = getActivity().findViewById(R.id.btn_notification);
    btnNotification.setOnClickListener(btnNotificationClick);

    callbackManager = CallbackManager.Factory.create();
    shareDialog = new ShareDialog(this);
    // this part is optional
    shareDialog.registerCallback(callbackManager, new FacebookCallback<Sharer.Result>() {
      @Override
      public void onSuccess(Sharer.Result result) {

      }

      @Override
      public void onCancel() {

      }

      @Override
      public void onError(FacebookException error) {

      }
    });

    SharePhoto photo = new SharePhoto.Builder()
            .setBitmap(image)
            .build();
    SharePhotoContent content = new SharePhotoContent.Builder()
            .addPhoto(photo)
            .build();
  }

  public Button.OnClickListener btnNotificationClick = new Button.OnClickListener() {
    @Override
    public void onClick(View view) {
      //將截圖Bitmap放入ImageView
      image = getScreenShot();
      imageView.setImageBitmap(image);
    }
  };

  @Override
  public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    callbackManager.onActivityResult(requestCode, resultCode, data);
  }

  private Bitmap getScreenShot() {
    //藉由View來Cache全螢幕畫面後放入Bitmap
    View mView = getActivity().getWindow().getDecorView();
    mView.setDrawingCacheEnabled(true);
    mView.buildDrawingCache();
    Bitmap mFullBitmap = mView.getDrawingCache();

    //取得系統狀態列高度
    Rect mRect = new Rect();
    getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(mRect);
    int mStatusBarHeight = mRect.top;

    //取得手機螢幕長寬尺寸
    int mPhoneWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
    int mPhoneHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

    //將狀態列的部分移除並建立新的Bitmap
    Bitmap mBitmap = Bitmap.createBitmap(mFullBitmap, 0, mStatusBarHeight, mPhoneWidth, mPhoneHeight - mStatusBarHeight);
    //將Cache的畫面清除
    mView.destroyDrawingCache();

    return mBitmap;
  }

}
