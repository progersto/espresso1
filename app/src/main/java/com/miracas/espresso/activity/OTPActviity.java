package com.miracas.espresso.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.miracas.R;
import com.miracas.espresso.rest.RestAuthWithOTP;
import com.miracas.espresso.rest.RestAuthWithOTPInterface;

import com.miracas.espresso.utils.PinEditText;
import com.miracas.espresso.widget.DTextView;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.miracas.espresso.activity.MobileVerificationActviity.PHONE_NUMBER;
import static com.miracas.espresso.utils.SharedStorage.JWT;


public class OTPActviity extends AppCompatActivity implements RestAuthWithOTPInterface {

    @BindView(R.id.verification_code)
    PinEditText pinview;
    @BindView(R.id.constraintLayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.textManually)
    TextView textManually;
    RestAuthWithOTP restAuthWithOTP;
    String phoneNumber = "";
    private StringBuilder sb = new StringBuilder();

    @BindView(R.id.txt1)
    DTextView txt1;
    @BindView(R.id.txt2)
    DTextView txt2;
    @BindView(R.id.txt3)
    DTextView txt3;
    @BindView(R.id.txt4)
    DTextView txt4;
    @BindView(R.id.txt5)
    DTextView txt5;
    @BindView(R.id.txt6)
    DTextView txt6;
    @BindView(R.id.txt7)
    DTextView txt7;
    @BindView(R.id.txt8)
    DTextView txt8;
    @BindView(R.id.txt9)
    DTextView txt9;
    @BindView(R.id.txt0)
    DTextView txt0;
    @BindView(R.id.lvDelete)
    LinearLayout lvDelete;
    @BindView(R.id.btnVerify)
    LinearLayout btnVerify;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp);
        ButterKnife.bind(this);

        if(savedInstanceState != null){
            phoneNumber = savedInstanceState.getString(PHONE_NUMBER);
            Log.d("PHONE_NUMBER","sis: "+phoneNumber);
        }else{
            if(getIntent().hasExtra(PHONE_NUMBER)){
                phoneNumber = getIntent().getStringExtra(PHONE_NUMBER);
            }
            Log.d("PHONE_NUMBER","ai: "+phoneNumber);
        }

        restAuthWithOTP = new RestAuthWithOTP(this);

//        pinview.setPinViewEventListener(new Pinview.PinViewEventListener() {
//            @Override
//            public void onDataEntered(Pinview pinview, boolean fromUser) {
//                textManually.setVisibility(View.GONE);
//                constraintLayout.setVisibility(View.VISIBLE);
//                restAuthWithOTP.auth(phoneNumber,pinview.getValue());
//            }
//        });
    }

    @OnClick({R.id.txt1, R.id.txt2, R.id.txt3, R.id.txt4, R.id.txt5, R.id.txt6, R.id.txt7, R.id.txt8, R.id.txt9, R.id.txt0, R.id.lvDelete, R.id.btnVerify})
    public void onViewClicked(View view) {
//        AnimationHelper.startAnimation(view);
        switch (view.getId()) {
            case R.id.txt1:
                addNumber("1");
                break;
            case R.id.txt2:
                addNumber("2");
                break;
            case R.id.txt3:
                addNumber("3");
                break;
            case R.id.txt4:
                addNumber("4");
                break;
            case R.id.txt5:
                addNumber("5");
                break;
            case R.id.txt6:
                addNumber("6");
                break;
            case R.id.txt7:
                addNumber("7");
                break;
            case R.id.txt8:
                addNumber("8");
                break;
            case R.id.txt9:
                addNumber("9");
                break;
            case R.id.txt0:
                addNumber("0");
                break;
            case R.id.lvDelete:
                if (sb.length() != 0){
                    sb.deleteCharAt(sb.length()-1);
                    pinview.setText(sb.toString());
                }
                break;
            case R.id.btnVerify:
                if (sb.length() == 6){
                    textManually.setVisibility(View.GONE);
                    constraintLayout.setVisibility(View.VISIBLE);
                    restAuthWithOTP.auth(phoneNumber,sb.toString());
                }else  Toast.makeText(this, "Enter all numbers!", Toast.LENGTH_SHORT).show();


//                Intent intent = new Intent(getBaseContext(),FacebookActivity.class);
////                intent.putExtra(JWT,response.getString("access_token"));
//                intent.putExtra(PHONE_NUMBER,phoneNumber);
//                startActivity(intent);
//                finish();

//                Intent intent = new Intent(this,FriendRequestActivity.class);
//                intent.putExtra(PHONE_NUMBER,phoneNumber);
//                startActivity(intent);
//                finish();
                break;
        }
    }

    public void addNumber(String text){
        if (sb.length()< 6){
            sb = sb.append(text);
            pinview.setText(sb.toString());
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PHONE_NUMBER,phoneNumber);
    }

    @Override
    public void onAuthSuccess(JSONObject response) {
        constraintLayout.setVisibility(View.INVISIBLE);
        if(!response.isNull("access_token")){
            try {
                Intent intent = new Intent(getBaseContext(),FacebookActivity.class);
                intent.putExtra(JWT,response.getString("access_token"));
                intent.putExtra(PHONE_NUMBER,phoneNumber);
                startActivity(intent);
                finish();
            } catch (JSONException e) {
                e.printStackTrace();
                showAuthError();
            }
        }else{
            showAuthError();
        }
    }

    @Override
    public void onAuthError(VolleyError error) {
        constraintLayout.setVisibility(View.INVISIBLE);
        showAuthError();
        finish();
        startActivity(new Intent(this,MobileVerificationActviity.class));
    }

    public void showAuthError(){
        Toast.makeText(getBaseContext(),R.string.auth_error,Toast.LENGTH_LONG).show();
    }
}
