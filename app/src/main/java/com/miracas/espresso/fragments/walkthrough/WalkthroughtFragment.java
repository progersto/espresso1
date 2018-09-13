package com.miracas.espresso.fragments.walkthrough;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miracas.R;
import com.miracas.espresso.adapters.CardItem;
import com.miracas.espresso.adapters.CardPagerAdapter;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.utils.RotateDownPageTransformer;
import com.miracas.espresso.widget.DButton;
import com.ravindu1024.indicatorlib.ViewPagerIndicator;
import butterknife.BindView;
import butterknife.ButterKnife;


public class WalkthroughtFragment extends BaseFragment {


    @BindView(R.id.vpSplash)
    ViewPager vpSplash;
    @BindView(R.id.indicatorSplash)
    ViewPagerIndicator indicatorSplash;
    @BindView(R.id.btnSplash)
    DButton btnSplash;

    private CardPagerAdapter mCardAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_walkthrought, container, false);

        ButterKnife.bind(this,view);

        onCreateToolbar(view, "Walkthrought", false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        //vpSplash = view.findViewById(R.id.vpSplash);

        //pager - the target ViewPager
        mCardAdapter = new CardPagerAdapter();
        mCardAdapter.addCardItem(new CardItem(R.string.title_1, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_2, R.string.text_1));
        mCardAdapter.addCardItem(new CardItem(R.string.title_3, R.string.text_1));

        vpSplash.setAdapter(mCardAdapter);

        vpSplash.setPageTransformer(true, new RotateDownPageTransformer());
        indicatorSplash.setPager(vpSplash);
        vpSplash.setOffscreenPageLimit(3);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        width = width * 10 / 720;

        vpSplash.setPageMargin(width);
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {

    }

//    @OnClick(R.id.btnSplash)
//    public void onViewClicked() {
//        Intent intent = new Intent(getBaseContext(), MobileVerificationActviity.class);
//        startActivity(intent);
//        finish();
//    }

}
