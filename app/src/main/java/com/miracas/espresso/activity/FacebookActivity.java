package com.miracas.espresso.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.miracas.R;
import com.miracas.espresso.adapters.FacebookAdapter;
import com.miracas.espresso.client.FacebookUserClient;
import com.miracas.espresso.client.NewUserClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.FacebookUser;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.dialogs.LoginDialog;
import com.miracas.espresso.model.FacebookModel;
import com.miracas.espresso.rest.RestFinishLoginWithFb;
import com.miracas.espresso.rest.RestFinishLoginWithFbInterface;
import com.miracas.espresso.rest.RestGetInvitations;
import com.miracas.espresso.rest.RestGetInvitationsInterface;
import com.miracas.espresso.utils.FacebookManager;
import com.miracas.espresso.utils.SharedStorage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import static com.miracas.espresso.activity.MobileVerificationActviity.PHONE_NUMBER;
import static com.miracas.espresso.model.FacebookModel.INVITATIONS;
import static com.miracas.espresso.utils.SharedStorage.JWT;

public class FacebookActivity extends AppCompatActivity implements IOnRequestCompleted,
        RestFinishLoginWithFbInterface, RestGetInvitationsInterface{

    public static final String FB_TOKEN = "FB_TOKEN";
    public static final String LOGIN = "login";
    public static final String ID = "id";
    public static final String email = "";

    @BindView(R.id.rvFacebook)
    RecyclerView rvFacebook;
    @BindView(R.id.login_button)
    LoginButton loginButton;
    @BindView(R.id.fb_login)
    ImageView fb_login;

    FacebookAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    String jwt = "";
    String fbToken = "";
    String phone_number = "";
    static FacebookUser user;
    static NewUserClient.Response userResponse;

    private CallbackManager callbackManager;
    private FacebookManager facebookManager;
    Activity activity;

    RestFinishLoginWithFb restFinishLoginWithFb;
    RestGetInvitations restGetInvitations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        ButterKnife.bind(this);

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", "ll");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        facebookManager = new FacebookManager(this);
        activity = this;

        if(savedInstanceState != null){
            jwt = savedInstanceState.getString(JWT);
            fbToken = savedInstanceState.getString(FB_TOKEN);
            phone_number = savedInstanceState.getString(PHONE_NUMBER);
        }else{
            if(getIntent().hasExtra(JWT) && getIntent().hasExtra(PHONE_NUMBER)){
                jwt = getIntent().getStringExtra(JWT);
                phone_number = getIntent().getStringExtra(PHONE_NUMBER);
            }
        }

        restFinishLoginWithFb = new RestFinishLoginWithFb(this);

        restGetInvitations = new RestGetInvitations(FacebookActivity.this);
        restGetInvitations.getInvitations(phone_number);
    }

    private void initRecyclerView(ArrayList<FacebookModel> list)
    {
        mLayoutManager =  new LinearLayoutManager(getBaseContext());
        rvFacebook.setLayoutManager(mLayoutManager);
        adapter = new FacebookAdapter(getBaseContext());
        adapter.addAll(list);
        rvFacebook.setAdapter(adapter);

        adapter.setEventListener(new FacebookAdapter.EventListener()
        {
            @Override
            public void onClick(int ledgerPosition)
            {
                Log.v("facebook - onClick", "onClick");
            }
        });
    }

    @OnClick(R.id.fb_login)
    public void onViewClicked() {
        initFacebookLogin();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void initFacebookLogin() {
        if (callbackManager != null) {
            loginButton.performClick();
            return;
        }

        loginButton = findViewById(R.id.login_button);
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().logInWithReadPermissions(
                this, Arrays.asList(
                        "email", "user_birthday",
                        "user_hometown", "user_location"));

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(final LoginResult loginResult) {
                        facebookManager.setLoginResult(loginResult);
                        fbToken = loginResult.getAccessToken().getToken();
                        Log.d("INVITATIONS","FA fb access token: "+fbToken);
                        FacebookUserClient facebookUserClient = new FacebookUserClient(
                                (IOnRequestCompleted) activity, fbToken);
                        facebookUserClient.execute();
//
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.v("facebook - onCancel", "onCancel");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.v("facebook - onError", exception.getMessage());
                    }
                });

    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {

        if (clientCode == ClientCodes.FACEBOOK_USER_CLIENT) {
            user = (FacebookUser) response;
            restFinishLoginWithFb.finishLogin(fbToken,jwt);
        } else if (clientCode == ClientCodes.NEW_USER_CLIENT && response != null) {
            userResponse = (NewUserClient.Response) response;
            //restFinishLoginWithFb.finishLogin(fbToken,jwt);
        }
    }

    public void startFriendRequestAcitivty(){
        Intent intent = new Intent(FacebookActivity.this,FriendRequestActivity.class);
        intent.putExtra(PHONE_NUMBER,phone_number);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(JWT,jwt);
        outState.putString(FB_TOKEN,fbToken);
        outState.putString(PHONE_NUMBER,phone_number);
    }

    @Override
    public void onLoginSuccess(JSONObject response) {
        Log.d("INVITATIONS","FA onLoginSuccess response: "+response.toString());

        try {
            if(!response.isNull(LOGIN)) {

                    JSONObject login = response.getJSONObject(LOGIN);
                    if(!login.isNull(ID)){
                        if (user != null) {
                            facebookManager.saveUserSession(user,login.getString(ID),jwt,phone_number);
                            startFriendRequestAcitivty();
                        } else if (userResponse != null) {
                            facebookManager.setUserId(userResponse.user.id);
                            startFriendRequestAcitivty();
                        } else {
                            showLoginError();
                        }
                    }else{
                        showLoginError();
                    }

            }else{
                showLoginError();
            }
            new SharedStorage(this).showPreferences(
                    "----------------------------------------> FINISH_LOGIN");
        } catch (JSONException e) {
            e.printStackTrace();
            showLoginError();
        }

    }

    @Override
    public void onLoginError(VolleyError error) {
        showLoginError();
    }

    public void showLoginError(){
        Toast.makeText(getBaseContext(),R.string.login_error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onGetInvitationsSuccess(JSONObject response) {
        Log.d("INVITATIONS"," FA onGetInvitationssuccess: "+response.toString());
        ArrayList<FacebookModel> facebookInvitationList = new ArrayList<>();

        try {
            if(!response.isNull(INVITATIONS)) {
                    JSONArray jsonInvitations = response.getJSONArray(INVITATIONS);

                    for(int i = 0; i < jsonInvitations.length(); i++){
                        FacebookModel fbm = FacebookModel.initFbModel(jsonInvitations.getJSONObject(i));
                        if(fbm != null) {
                            facebookInvitationList.add(fbm);
                        }else{return;}
                    }
                    initRecyclerView(facebookInvitationList);
            }
        } catch (JSONException e) {
            Log.d("INVITATIONS","catch: "+e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onGetInvitationsError(VolleyError error) {
        Log.d("INVITATIONS","error: "+error.toString());
    }
}
