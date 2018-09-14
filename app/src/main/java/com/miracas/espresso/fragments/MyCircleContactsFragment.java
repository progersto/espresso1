package com.miracas.espresso.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.miracas.R;
import com.miracas.espresso.activity.MainActivity;
import com.miracas.espresso.activity.contacts.ContactActivity;
import com.miracas.espresso.adapters.MycircleContactsAdapter;
import com.miracas.espresso.model.ContactModel;
import com.miracas.espresso.model.FacebookModel;
import com.miracas.espresso.rest.RestGetInvitations;
import com.miracas.espresso.rest.RestGetInvitationsInterface;
import com.miracas.espresso.rest.RestGetUserNetwork;
import com.miracas.espresso.rest.RestGetUserNetworkInterface;
import com.miracas.espresso.utils.SharedStorage;
import com.miracas.espresso.widget.DEditText;
import com.miracas.espresso.widget.DTextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.miracas.espresso.activity.contacts.ContactActivity.CONTACT_NAME;
import static com.miracas.espresso.model.ContactModel.CONTACTS;
import static com.miracas.espresso.model.ContactModel.USER_NETWORK;
import static com.miracas.espresso.model.FacebookModel.INVITATIONS;

public class MyCircleContactsFragment extends BaseFragment implements RestGetUserNetworkInterface,
        RestGetInvitationsInterface{

    public static final int GET_CONTACTS_REQUEST = 1131;

    Unbinder unbinder;
    @BindView(R.id.rvContacts)
    RecyclerView rvContacts;
//    @BindView(R.id.btn_add)
//    ImageView btn_add;
    @BindView(R.id.loading)
    RelativeLayout progress_bar;
    @BindView(R.id.pending_requests)
    DTextView pending_requests;
    @BindView(R.id.search)
    DEditText search;

    MycircleContactsAdapter adapter;
    RestGetUserNetwork restGetUserNetwork;
    RestGetInvitations restGetInvitations;
    private RecyclerView.LayoutManager mLayoutManager;
    ArrayList<ContactModel> contactModelArrayList = new ArrayList<ContactModel>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_mycircle_contacts, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        restGetUserNetwork = new RestGetUserNetwork(this); restGetUserNetwork.getUserNetwork(
                new SharedStorage(getActivity()).getValue(getString(R.string.shared_storage_jwt)));

        restGetInvitations = new RestGetInvitations(this);

        return rootView;
    }

    private void initRecyclerView(ArrayList<ContactModel> list) {
        mLayoutManager = new LinearLayoutManager(getContext());
        rvContacts.setLayoutManager(mLayoutManager);
        adapter = new MycircleContactsAdapter(getActivity());
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
        rvContacts.setAdapter(adapter);

        adapter.setEventListener(new MycircleContactsAdapter.EventListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @OnClick(R.id.constraintLayout3)
    public void onBronzeClicked(){
        ((MainActivity)getActivity()).switchFragment(R.id.nav_bronz, true, null);
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        onCreateToolbar(getView() ,"User Network", false);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {

    }

    @OnClick(R.id.inviteFriends)
    public void onAddClick(){
        startActivityForResult(new Intent(getContext(), ContactActivity.class),GET_CONTACTS_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_CONTACTS_REQUEST  && resultCode  == RESULT_OK){
            Toast.makeText(getContext(),
                data.getStringExtra(CONTACT_NAME),
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onGetUsserNetworkSuccess(JSONObject response) {
        Log.d("USER_NETWORK","response: "+response);
        hideProgressBar();
        if(!response.isNull(USER_NETWORK)){
            try {
                JSONObject userNetwork = response.getJSONObject(USER_NETWORK);
                if(!userNetwork.isNull(CONTACTS)){
                    try {
                        JSONArray jsonArray = userNetwork.getJSONArray(CONTACTS);

                        for(int x = 0; x < jsonArray.length(); x++){
                            ContactModel cm = ContactModel.initContactModel(jsonArray.getJSONObject(x));
                            if(cm != null){
                                contactModelArrayList.add(cm);
                            }else{
                                showUserNetworkErroMessage();
                                return;
                            }
                        }
                        pending_requests.setText(contactModelArrayList.size()+"");
                        initRecyclerView(contactModelArrayList);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        showUserNetworkErroMessage();
                    }
                }else{
                    showUserNetworkErroMessage();
                }
            } catch (JSONException e) {
                showUserNetworkErroMessage();
            }
        }else {
            showUserNetworkErroMessage();
        }
    }

    @Override
    public void onGetUserNetworkError(VolleyError error) {
        Log.d("USER_NETWORK","error: "+error.toString());
        hideProgressBar();
    }

    public void showUserNetworkErroMessage(){
        Toast.makeText(getContext(),R.string.user_network_fetched_err,Toast.LENGTH_LONG).show();
    }

    public ArrayList<ContactModel> search(String sub) {
        ArrayList<ContactModel> result = new ArrayList<ContactModel>();

        for (ContactModel x : contactModelArrayList) {
            if (x.getName().toLowerCase().contains(sub.toLowerCase())) {
                result.add(x);
            }
        }
        return result;
    }

    public void hideProgressBar(){
        progress_bar.setVisibility(View.GONE);
    }


    @Override
    public void onGetInvitationsSuccess(JSONObject response) {
        try {
            if(!response.isNull(INVITATIONS)) {
                JSONArray jsonInvitations = response.getJSONArray(INVITATIONS);
                if(jsonInvitations.length() > 0){
                    pending_requests.setText(contactModelArrayList.size()+"");
                }else{
                    pending_requests.setVisibility(View.GONE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onGetInvitationsError(VolleyError error) {}
}