package com.miracas.espresso.activity.contacts;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.miracas.R;
import com.miracas.espresso.rest.RestSendInvitations;
import com.miracas.espresso.rest.RestSendInvitationsInterface;
import com.miracas.espresso.utils.SharedStorage;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import static com.miracas.espresso.rest.RestGetFreebiePictures.COULDNT_INVITE;
import static com.miracas.espresso.rest.RestGetFreebiePictures.INVITED;

public class ContactActivity extends AppCompatActivity implements ContactListCursorInterface,
        ContactSelectedInterface, RestSendInvitationsInterface {

    public static final String CONTACT_NAME = "contact_name";

    String[] permissions;

    ContactAdapter contactAdapter;
    RecyclerView contactRecycler;
    ArrayList<User> contactList;
    LinearLayout sendInvitations;

    ArrayList<User> selectedContacts = new ArrayList<>();

    RestSendInvitations restSendInvitations;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_contacts);
        getSupportActionBar().setTitle(R.string.pick_a_contact);

        restSendInvitations = new RestSendInvitations(this);

        permissions = new String[]{Manifest.permission.READ_CONTACTS};
        contactList = new ArrayList<User>();

        contactRecycler = findViewById(R.id.recycler);
        sendInvitations = findViewById(R.id.sendInvitaions);
        initContactAdapter();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED){
                requestPermissions(permissions,111);
            }else{
                getContacts();
            }
        }

        sendInvitations.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(selectedContacts.size() > 0) {
                    restSendInvitations.sendInvitations(
                            new SharedStorage(ContactActivity.this).getValue(getString(R.string.shared_storage_jwt)),
                            selectedContacts
                    );
                }else{
                    Toast.makeText(getBaseContext(),R.string.please_select_first,Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for(int i = 0; i < permissions.length; i ++){
            String per = permissions[i];
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                getContacts();
            }else{
                Toast.makeText(getBaseContext(),R.string.contact_permission,Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    public void getContacts(){
        new ContactListCursor(this,this).execute();
    }

    @Override
    public void onGetContactList(ArrayList<User> contacts) {
        contactList = contacts;
        initContactAdapter();
    }

    @Override
    public void onContactSelected(User friend, boolean action) {
        if(action){
            selectedContacts.add(friend);
        }else{
            selectedContacts.remove(friend);
        }
    }

    public void initContactAdapter(){
        contactAdapter = new ContactAdapter(contactList,this,this);
        contactRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));
        contactRecycler.setAdapter(contactAdapter);
        contactAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSendInvitationsSuccess(JSONObject response) {

        try {
            if(!response.isNull(INVITED) && !response.isNull(COULDNT_INVITE)){
                int invitedLength = response.getJSONArray(INVITED).length();
                int couldntInvite = response.getJSONArray(COULDNT_INVITE).length();

                if( invitedLength > 0
                        && couldntInvite <= 0){
                    Toast.makeText(getBaseContext(),R.string.invitation_success,Toast.LENGTH_LONG).show();
                }else if(invitedLength > 0 && couldntInvite > 0) {
                    Toast.makeText(getBaseContext(),
                            R.string.invitation_partial_success
                            + response.getJSONArray(COULDNT_INVITE).toString(),Toast.LENGTH_LONG).show();
                }else{
                    showInviteError();
                }

                finish();

            }else{
                showInviteError();
            }
        } catch (JSONException e) {
            showInviteError();
        }

    }

    @Override
    public void onSendInvitationsError(VolleyError error) {
        showInviteError();
    }

    public void showInviteError(){
        Toast.makeText(getBaseContext(),R.string.invitation_err,Toast.LENGTH_LONG).show();
    }
}

