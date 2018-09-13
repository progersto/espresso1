package com.miracas.espresso.activity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.miracas.R;
import com.miracas.espresso.rest.RestGenerateOTP;
import com.miracas.espresso.rest.RestGenerateOTPInterface;
import com.miracas.espresso.utils.AnimationHelper;
import com.miracas.espresso.utils.MyEditText;
import com.miracas.espresso.widget.DTextView;
import org.json.JSONObject;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MobileVerificationActviity extends AppCompatActivity implements RestGenerateOTPInterface,
        MyEditText.OnCursorChangedInterface{

    public static final String PHONE_NUMBER = "PHONE_NUMBER";

    @BindView(R.id.txtMobileNumber)
    MyEditText txtMobileNumber;
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

    RestGenerateOTP restGenerateOTP;
    String phoneNumber = "";

    int position = 0;
    int oldPosition = 0;
    static boolean delete = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile_verification);
        ButterKnife.bind(this);

        disableSoftInputFromAppearing(txtMobileNumber);
        txtMobileNumber.setListener(this);
        txtMobileNumber.getBackground()
                .setColorFilter(getResources().getColor(R.color.colorTextOTP), PorterDuff.Mode.SRC_IN);

        restGenerateOTP = new RestGenerateOTP(this);

    }

    @OnClick({R.id.txt1, R.id.txt2, R.id.txt3, R.id.txt4, R.id.txt5, R.id.txt6, R.id.txt7, R.id.txt8, R.id.txt9, R.id.txt0, R.id.lvDelete, R.id.btnVerify})
    public void onViewClicked(View view) {
//        AnimationHelper.startAnimation(view);
        switch (view.getId()) {
            case R.id.txt1:
                addNumber(txt1);
                break;
            case R.id.txt2:
                addNumber(txt2);
                break;
            case R.id.txt3:
                addNumber(txt3);
                break;
            case R.id.txt4:
                addNumber(txt4);
                break;
            case R.id.txt5:
                addNumber(txt5);
                break;
            case R.id.txt6:
                addNumber(txt6);
                break;
            case R.id.txt7:
                addNumber(txt7);
                break;
            case R.id.txt8:
                addNumber(txt8);
                break;
            case R.id.txt9:
                addNumber(txt9);
                break;
            case R.id.txt0:
                addNumber(txt0);
                break;
            case R.id.lvDelete:
                if (txtMobileNumber.getText().length() != 0){
                    if(position <= txtMobileNumber.getText().length() && position >= 0) {
                        StringBuilder stringBuilder = new StringBuilder(txtMobileNumber.getText().toString());
                        if(position > 0) {
                            int pos = position;
                            delete = true;
                            stringBuilder.deleteCharAt(pos - 1);
                            txtMobileNumber.setText(stringBuilder.toString());
                        }

                        Log.d("POSITION","delete char at; "+position +" ,newString: "+stringBuilder.toString());
                    }
                }
                break;
            case R.id.btnVerify:
                    if(txtMobileNumber.getText() != null){
                        if(txtMobileNumber.getText().toString().length() > 0) {
                            phoneNumber = txtMobileNumber.getText().toString();
                            restGenerateOTP.generateOTP(phoneNumber);
                        }else{
                            Toast.makeText(getBaseContext(),R.string.phone_verification_empty,Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getBaseContext(),R.string.phone_verification_empty,Toast.LENGTH_LONG).show();
                    }
                break;
        }
    }


    @Override
    public void onOTPgenerate(JSONObject response) {
        Log.d("OTP","success: "+response.toString());
        Intent intent = new Intent(getBaseContext(), OTPActviity.class);
        intent.putExtra(PHONE_NUMBER,phoneNumber);
        startActivity(intent);
        finish();
    }

    @Override
    public void onOTPerror(VolleyError error) {
        Log.d("OTP","error: "+error.toString());
        Toast.makeText(getBaseContext(),R.string.generate_otp_error,Toast.LENGTH_LONG).show();
    }

    public static void disableSoftInputFromAppearing(EditText editText) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { // API 21
            editText.setShowSoftInputOnFocus(false);
        } else { // API 11-20
            editText.setTextIsSelectable(true);
        }
    }

    public void addNumber(DTextView textView){
        Log.d("POSITION","size: "+txtMobileNumber.getText().length()+ " ,p: "+position);
        txtMobileNumber.getText().insert(position,textView.getText());
    }

    @Override
    public void onCursorPositionChanged(int position) {
        Log.d("POSITION","position: "+this.position);
        if(!delete) {
            this.position = position;
        }else{
            this.position--;
            Log.d("POSITION","after decrement position: "+this.position);
            delete = false;
            txtMobileNumber.setSelection(this.position);
        }
    }
}
