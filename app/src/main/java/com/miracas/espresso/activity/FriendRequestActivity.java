package com.miracas.espresso.activity;

/**
 * Created by Alpesh on 8/8/2018.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.miracas.R;
import com.miracas.espresso.adapters.FriendRequestAdapter;
import com.miracas.espresso.model.FacebookModel;
import com.miracas.espresso.rest.RestApprove;
import com.miracas.espresso.rest.RestApproveInterface;
import com.miracas.espresso.rest.RestGetInvitations;
import com.miracas.espresso.rest.RestGetInvitationsInterface;
import com.miracas.espresso.rest.RestReject;
import com.miracas.espresso.rest.RestRejectInterface;
import com.miracas.espresso.utils.SharedStorage;
import com.miracas.espresso.widget.DTextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import static com.miracas.espresso.activity.MobileVerificationActviity.PHONE_NUMBER;
import static com.miracas.espresso.adapters.FriendRequestAdapter.FRIEND_APPROVE;
import static com.miracas.espresso.model.FacebookModel.INVITATIONS;
import static com.miracas.espresso.rest.RestApprove.MESSAGE;
import static com.miracas.espresso.rest.RestApprove.OK;
import static com.miracas.espresso.rest.RestReject.REJECTED;

public class FriendRequestActivity extends AppCompatActivity implements RestGetInvitationsInterface,
        RestApproveInterface, RestRejectInterface {

    Unbinder unbinder;
    @BindView(R.id.rvRequest)
    RecyclerView rvRequest;
    @BindView(R.id.approve)
    ImageView btnApprove;
    @BindView(R.id.approveAll)
    DTextView btnApproveAll;
    @BindView(R.id.skip)
    DTextView btnSkip;

    FriendRequestAdapter adapter;
    RestGetInvitations restGetInvitations;
    private RecyclerView.LayoutManager mLayoutManager;

    String phone_number = "";

    RestApprove restApprove;
    RestReject restReject;
    ArrayList<FacebookModel> facebookInvitationList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("INVITATIONS", "FRA onCreate");
        setContentView(R.layout.fragment_friend_request);

        if (savedInstanceState != null) {
            phone_number = savedInstanceState.getString(PHONE_NUMBER);
        } else {
            phone_number = getIntent().getStringExtra(PHONE_NUMBER);
        }

        restApprove = new RestApprove(this);
        restReject = new RestReject(this);
        restGetInvitations = new RestGetInvitations(FriendRequestActivity.this);
        restGetInvitations.getInvitations(phone_number);

        unbinder = ButterKnife.bind(this, this);
    }

    private void initRecyclerView(ArrayList<FacebookModel> list) {
        mLayoutManager = new LinearLayoutManager(getBaseContext());
        rvRequest.setLayoutManager(mLayoutManager);
        adapter = new FriendRequestAdapter(getBaseContext());
        adapter.addAll(list);
        rvRequest.setAdapter(adapter);

        adapter.setEventListener(new FriendRequestAdapter.EventListener() {
            @Override
            public void onClick(FacebookModel facebookModel,int position,int action) {
                String token = new SharedStorage(FriendRequestActivity.this)
                        .getValue(getString(R.string.shared_storage_jwt));

                if(action == FRIEND_APPROVE){
                    restApprove.approveOne(token,facebookModel,position );
                }else{
                    restReject.reject(token,facebookModel.getInvitationId(),position);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onGetInvitationsSuccess(JSONObject response) {
        Log.d("INVITATIONS", "FRA ON GET INVITATIONS SUCCESS : " + response.toString());

        try {
            if (!response.isNull(INVITATIONS)) {
                JSONArray jsonInvitations = response.getJSONArray(INVITATIONS);

                for (int i = 0; i < jsonInvitations.length(); i++) {
                    FacebookModel fbm = FacebookModel.initFbModel(jsonInvitations.getJSONObject(i));
                    if (fbm != null) {
                        facebookInvitationList.add(fbm);
                    } else {
                        return;
                    }
                }
                initRecyclerView(facebookInvitationList);
            }
        } catch (JSONException e) {
            Log.d("INVITATIONS", "catch: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public void onGetInvitationsError(VolleyError error) {
        Log.d("INVITATIONS", "error: " + error.toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(PHONE_NUMBER, phone_number);
    }

    @OnClick(R.id.skip)
    public void skipClicked() {
        startMainActivity();
    }

    @OnClick(R.id.approveAll)
    public void approveAll() {
        restApprove.approveAll(new SharedStorage(this).getValue(getString(R.string.shared_storage_jwt)));
    }

    @OnClick(R.id.approve)
    public void approve() {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startMainActivity();
    }

    public void startMainActivity(){
        finish();
        startActivity(new Intent(this,MainActivity.class));
    }

    @Override
    public void onApproveAllSuccess(JSONObject response) {
        try {
            if(!response.isNull(MESSAGE)){
                if(response.getString(MESSAGE) == OK){
                    Toast.makeText(getBaseContext(),R.string.you_approved_all,Toast.LENGTH_LONG).show();
                    startMainActivity();
                }else{
                    showApproveAllError();
                }
            }else{
                showApproveAllError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showApproveAllError();
        }

    }

    public void showApproveAllError(){
        Toast.makeText(getBaseContext(),R.string.you_approved_all_error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onApproveAllError(VolleyError error) {
        showApproveAllError();
    }

    @Override
    public void onApproveOneSuccess(JSONObject response, int position, FacebookModel facebookModel) {
        Log.d("APPROVE","one success: "+response.toString()+ " position: "+position +
        " fb: "+facebookModel.toString());

        try {
            if(!response.isNull(MESSAGE)){
                if(response.getString(MESSAGE).equals(OK)){
                    Toast.makeText(getBaseContext(),R.string.you_approved_friend,Toast.LENGTH_LONG).show();
                    removeFromList(position);
                }else{
                    showOneApproveError();
                }
            }else{
                showOneApproveError();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            showOneApproveError();
        }
    }

    @Override
    public void onApproveOneError(VolleyError error, int position, FacebookModel facebookModel) {
        Log.d("APPROVE","one err: "+error.toString()+ " position: "+position +
                " fb: "+facebookModel.toString());
    }

    public void showOneApproveError(){
        Toast.makeText(getBaseContext(),R.string.you_approved_friend_error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRejectSuccess(JSONObject response,int position) {
        try {
            if(!response.isNull(REJECTED)){
                    if(response.getBoolean(REJECTED) == true){
                        removeFromList(position);
                    }else{
                        showRejectError();
                    }
                }else{
                    showRejectError();
                }
        } catch (JSONException e) {
            e.printStackTrace();
            showRejectError();
        }
    }

    @Override
    public void onRejectError(VolleyError error, int position) {
        showRejectError();
    }

    public void showRejectError(){
        Toast.makeText(getBaseContext(),R.string.you_reject_friend_error,Toast.LENGTH_LONG).show();
    }

    public void removeFromList(int position){
        facebookInvitationList.remove(position);
        adapter.clear();adapter.addAll(facebookInvitationList);
        Toast.makeText(getBaseContext(),R.string.you_approved_friend,Toast.LENGTH_LONG).show();
        adapter.notifyDataSetChanged();
    }
}


