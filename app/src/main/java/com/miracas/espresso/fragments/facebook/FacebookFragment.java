package com.miracas.espresso.fragments.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.miracas.R;
import com.miracas.espresso.adapters.FacebookAdapter;
import com.miracas.espresso.fragments.BaseFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Alpesh on 8/23/2018.
 */

public class FacebookFragment extends BaseFragment {

    @BindView(R.id.rvFacebook)
    RecyclerView rvFacebook;
    FacebookAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_facebook, container, false);
        ButterKnife.bind(this,view);
        onCreateToolbar(view, "Facebook", false);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView()
    {
        mLayoutManager =  new LinearLayoutManager(getContext());
        rvFacebook.setLayoutManager(mLayoutManager);
        adapter = new FacebookAdapter(getContext());
        rvFacebook.setAdapter(adapter);

        adapter.setEventListener(new FacebookAdapter.EventListener()
        {
            @Override
            public void onClick(int ledgerPosition)
            {

            }
        });


    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {

    }
}
