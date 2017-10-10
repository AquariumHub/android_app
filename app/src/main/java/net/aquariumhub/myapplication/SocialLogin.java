package net.aquariumhub.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookActivity;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SocialLogin extends AppCompatActivity {

  private final String TAG = "SocialLogin";

  /**
   * Facebook instances
   */
  private LoginManager loginManager;
  private CallbackManager callbackManager;
  private ProfileTracker profileTracker;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.social_login);

    // init LoginManager & CallbackManager
    loginManager = LoginManager.getInstance();
    callbackManager = CallbackManager.Factory.create();

    findViewById(R.id.fb_login).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        loginFB();
      }
    });

    if (Profile.getCurrentProfile() != null) {
      Profile profile = Profile.getCurrentProfile();
      // get the user profile picture
      Uri uri_userPhoto = profile.getProfilePictureUri(300, 300);
      String id = profile.getId();
      String name = profile.getName();
      Log.d(TAG, "Facebook userPhoto: " + uri_userPhoto);
      Log.d(TAG, "Facebook id: " + id);
      Log.d(TAG, "Facebook name: " + name);
    } else {
      Log.d(TAG, "Profile.getCurrentProfile() == null");
      profileTracker = new ProfileTracker() {
        @Override
        protected void onCurrentProfileChanged(Profile oldProfile, Profile currentProfile) {
          Log.d(TAG, "facebook - profile" + currentProfile.getFirstName());
          profileTracker.stopTracking();
        }
      };
      // no need to call startTracking() on mProfileTracker
      // because it is called by its constructor, internally.
    }

    /*if (AccessToken.getCurrentAccessToken() != null) {
            Log.d(TAG, "Facebook getApplicationId: " + AccessToken.getCurrentAccessToken().getApplicationId());
            Log.d(TAG, "Facebook getUserId: " + AccessToken.getCurrentAccessToken().getUserId());
            Log.d(TAG, "Facebook getExpires: " + AccessToken.getCurrentAccessToken().getExpires());
            Log.d(TAG, "Facebook getLastRefresh: " + AccessToken.getCurrentAccessToken().getLastRefresh());
            Log.d(TAG, "Facebook getToken: " + AccessToken.getCurrentAccessToken().getToken());
            Log.d(TAG, "Facebook getSource: " + AccessToken.getCurrentAccessToken().getSource());
        }*/
  }

  private void loginFB() {
    // set behavior of FB login; default: NATIVE_WITH_FALLBACK
    /**
     * 1. NATIVE_WITH_FALLBACK
     * 2. NATIVE_ONLY
     * 3. KATANA_ONLY
     * 4. WEB_ONLY
     * 5. WEB_VIEW_ONLY
     * 6. DEVICE_AUTH
     */

    loginManager.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
    // set the permissions get from users
    List<String> permissions = new ArrayList<>();
    // the basic three permissions can get from users without checked by FB
    permissions.add("public_profile");
    permissions.add("email");
    permissions.add("user_friends");

    // set read permissions
    loginManager.logInWithReadPermissions(this, permissions);
    loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
      @Override
      public void onSuccess(LoginResult loginResult) {

        // get user information via GraphRequest
        GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
          @Override
          public void onCompleted(JSONObject object, GraphResponse response) {
            try {
              if (response.getConnection().getResponseCode() == 200) {
                long id = object.getLong("id");
                String name = object.getString("name");
                String email = object.getString("email");
                Log.d(TAG, "Facebook id:" + id);
                Log.d(TAG, "Facebook name:" + name);
                Log.d(TAG, "Facebook email:" + email);
                // get user profile
                Profile profile = Profile.getCurrentProfile();
                // set profile size
                Uri uri_userPhoto = profile.getProfilePictureUri(300, 300);
              }
            } catch (IOException e) {
              e.printStackTrace();
            } catch (JSONException e) {
              e.printStackTrace();
            }
          }
        });
      }

      @Override
      public void onCancel() {
        Toast.makeText(SocialLogin.this, "Facebook login cancelled", Toast.LENGTH_LONG).show();
      }

      @Override
      public void onError(FacebookException error) {
        Toast.makeText(SocialLogin.this, "Facebook login error: " + error.toString(), Toast.LENGTH_LONG).show();
      }
    });
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    // if you don't add following block,
    // your registered `FacebookCallback` won't be called
    if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
      return;
    }
  }

}
