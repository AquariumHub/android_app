package net.aquariumhub.myapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static android.content.Context.NOTIFICATION_SERVICE;

public class FragmentTabHistory extends Fragment {

  Button btnNotification;
  private Intent intent;
  private static final int NOTIFICATION_ID = 0;
  private NotificationManager notificationManger;
  private Notification notification;

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.tab_history, container, false);
  }

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    notificationManger = (NotificationManager)getActivity().getSystemService(NOTIFICATION_SERVICE);

    btnNotification = getActivity().findViewById(R.id.btn_notification);
    btnNotification.setOnClickListener(btnNotificationClick);

    intent = new Intent();
    intent.setClass(getActivity(), HandleNotification.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
    PendingIntent pendingIntent =
            PendingIntent.getActivity(getActivity(), NOTIFICATION_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    notification = new Notification.Builder(getActivity())
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle("Hi")
            .setContentText("Nice to meet you.")
            .setContentIntent(pendingIntent)
            .setDefaults(Notification.DEFAULT_ALL)
            .build(); // available from API level 11 and onwards
    notification.flags = Notification.FLAG_AUTO_CANCEL;

  }

  Button.OnClickListener btnNotificationClick = new Button.OnClickListener() {
    @Override
    public void onClick(View view) {
      notificationManger.notify(0, notification);
    }
  };
}
