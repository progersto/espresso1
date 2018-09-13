package com.miracas.espresso.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.crashlytics.android.Crashlytics;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.client.ActiveKeywordClient;
import com.miracas.espresso.client.FacebookUserClient;
import com.miracas.espresso.client.GetCountClient;
import com.miracas.espresso.client.GetProductOptionDetailsClient;
import com.miracas.espresso.client.GlobalsClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.client.responses.Quote;
import com.miracas.espresso.client.shares.ProductLinkOpenedClient;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.dialogs.LoginDialog;
import com.miracas.espresso.dialogs.ReviewDialog;
import com.miracas.espresso.fragments.MyCircleContactsFragment;
import com.miracas.espresso.fragments.SectionPageFragment;
import com.miracas.espresso.fragments.cart.CartFragment;
import com.miracas.espresso.fragments.expresso.MyExpressoFragment;
import com.miracas.espresso.fragments.home.DailyCollectionFragment;
import com.miracas.espresso.fragments.home.DirectPurchaseFragment;
import com.miracas.espresso.fragments.orders.ListOrdersFragment;
import com.miracas.espresso.fragments.shop.ShopFragment;
import com.miracas.espresso.fragments.shop.ShopProductsFragment;
import com.miracas.espresso.utils.FacebookManager;
import com.miracas.espresso.utils.SharedStorage;
import com.miracas.espresso.utils.SharedStorageHelper;
import com.pushwoosh.Pushwoosh;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import io.fabric.sdk.android.Fabric;
import static com.miracas.espresso.utils.SharedStorage.JWT;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, IOnRequestCompleted,
        LoginDialog.OnLoginDialogListener{

    private Activity activity;
    private CallbackManager callbackManager;
    private FacebookManager facebookManager;
    private LoginButton loginButton;
    private TextView quoteView;
    private String quote = "";
    private List<Quote> quotes = new ArrayList<>();

    private boolean sharedProductInvoked;
    private Product sharedProduct;
    private int sharedKeywordID;
    private String sharedPrestashopID;

    private int brewingProductCount = 0;

    public static final String FIRST_RUN = "FIRST_RUN";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Fabric.with(this, new Crashlytics());

        setAPIEndpoints();

        facebookManager = new FacebookManager(this);
        if (facebookManager.checkLoginStatus()) {
            facebookManager.displayUserDetails();
        }

        if (savedInstanceState == null) {
            switchFragment(R.id.nav_home, false,null);
        }

        handleIntentData();

        getSupportFragmentManager().addOnBackStackChangedListener(
                () -> {
                    FragmentManager manager = getSupportFragmentManager();
                    if (manager != null) {
                        Fragment currFrag = manager.findFragmentById(R.id.content_frame);
                        currFrag.onResume();
                    }
                });

        activity = this;

        renderLoginButtons();
        handleGlobals();
        handleReviewDialog();
        Pushwoosh.getInstance().registerForPushNotifications();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            FragmentManager fm = getSupportFragmentManager();
            if (fm.getBackStackEntryCount() > 0) {
                Fragment currFrag = fm.findFragmentById(R.id.content_frame);
                currFrag.onDestroy();
                fm.popBackStackImmediate();
                System.gc();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switchFragment(id, true,null);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void setAPIEndpoints() {
        Settings.ENDPOINT_RAILS = getString(R.string.rails_server_developemnt);
        Settings.ENDPOINT_PRESTASHOP = getString(R.string.php_server);
    }

    private void handleGlobals() {
        GlobalsClient client = new GlobalsClient(this);
        client.execute();

        ActiveKeywordClient client1 = new ActiveKeywordClient(this);
        client1.execute();
    }

    private void handleIntentData() {
        sharedProductInvoked = false;

        Intent intent = getIntent();
        boolean firstRun = intent.getBooleanExtra(FIRST_RUN,true);

        Uri data = intent.getData();
        String event = intent.getStringExtra("event");

        if (data != null) {
            String productID = data.getQueryParameter("id_product");
            String prestashopID = data.getQueryParameter("id_product_prestashop");
            String keywordID = data.getQueryParameter("id_keyword");
            String shareID = data.getQueryParameter("share_id");

            if (data.getHost().equals("brewed")) {
                // PushWoosh initiated
                //handleSplash(false);
                switchFragment(R.id.nav_pot, true,null);

            } else if (productID != null && !productID.isEmpty() &&
                    prestashopID != null && !prestashopID.isEmpty() &&
                    keywordID != null && !keywordID.isEmpty() &&
                    shareID != null && !shareID.isEmpty()) {
                // Handle shared url
                //handleSplash(false);
                sharedKeywordID = Integer.valueOf(keywordID);
                sharedPrestashopID = prestashopID;
                getProductDetailsAndOpenPage(prestashopID, shareID);
                sharedProductInvoked = true;
            }

        } else if (event != null && event.equals("brewed")) {
            // Rails initiated
            //handleSplash(false);
            switchFragment(R.id.nav_pot, true,null);

        }else if(!firstRun){
            //handleSplash(false);
        } else {
            // Normal startup
           //handleSplash(true);
        }
    }

    private void getProductDetailsAndOpenPage(String productID, String shareID) {
        ProductLinkOpenedClient linkOpenedClient = new ProductLinkOpenedClient(this, shareID);
        linkOpenedClient.execute();
        GetProductOptionDetailsClient client = new GetProductOptionDetailsClient(this, productID);
        client.execute();
    }

    private void handleReviewDialog() {
        SharedStorageHelper helper = new SharedStorageHelper(this);
        int appOpens = helper.getAppOpens();
        appOpens += 1;

        int threshold = helper.getReviewThreshold();
        if (appOpens % threshold == 0) {
            DialogFragment reviewDialog = ReviewDialog.newInstance();
            reviewDialog.show(getSupportFragmentManager(), "dialog");
        }

        helper.setAppOpens(appOpens);
    }

    public void showLoginDialog() {
        DialogFragment showLoginDialog = LoginDialog.newInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString("QUOTE", quote);
        showLoginDialog.show(getSupportFragmentManager(), "dialog");
        showLoginDialog.setArguments(bundle);
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
                        FacebookUserClient facebookUserClient = new FacebookUserClient(
                                (IOnRequestCompleted) activity, loginResult.getAccessToken().getToken());
                        facebookUserClient.execute();
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Log.v("facebook - onError", exception.getMessage());
                    }
                });

    }

    private void renderLoginButtons() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem login = menu.findItem(R.id.nav_login);
        MenuItem myEspresso = menu.findItem(R.id.nav_pot);
        MenuItem cart = menu.findItem(R.id.nav_cart);
        MenuItem user_network = menu.findItem(R.id.nav_user_network);
        MenuItem bronz =menu.findItem(R.id.nav_bronz);
        MenuItem myOrders = menu.findItem(R.id.nav_orders);

        if (facebookManager.checkLoginStatus()) {
            login.setTitle("Logout");
            myEspresso.setVisible(true);
            cart.setVisible(true);
            //facebook.setVisible(true);
            myOrders.setVisible(true);
            GetCountClient client = new GetCountClient(this);
            client.execute();

        } else {
            login.setTitle("Login");
            myEspresso.setVisible(false);
            cart.setVisible(false);
            //facebook.setVisible(false);
            myOrders.setVisible(false);
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    public void switchFragment(int menuID, boolean animate, Bundle arguments ) {
        Fragment fragment = null;

        if (menuID == R.id.nav_home) {
            fragment = new SectionPageFragment();

        } else if(menuID == R.id.nav_pot) {
            if (facebookManager.checkLoginStatus()) {
                fragment = new MyExpressoFragment();
            } else {
                showLoginDialog();
            }

        } else if (menuID == R.id.nav_daily_collection) {
            fragment = new DailyCollectionFragment();
            Bundle bundle = new Bundle();
            bundle.putString("QUOTES", new Gson().toJson(quotes));
            fragment.setArguments(bundle);

        } else if (menuID == R.id.nav_cart) {
            if (facebookManager.checkLoginStatus()) {
                fragment = new CartFragment();
            } else {
                showLoginDialog();
            }

        } else if(menuID == R.id.nav_login) {
            if (facebookManager.checkLoginStatus()) {
                LoginManager.getInstance().logOut();
                clearJWT();
                facebookManager.logout();
                renderLoginButtons();
                Toast.makeText(getApplicationContext(), "You have been logged out",
                        Toast.LENGTH_SHORT).show();
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    Fragment currentFragment = manager.findFragmentById(R.id.content_frame);
                    if (currentFragment != null && currentFragment instanceof SectionPageFragment) {
                        SectionPageFragment sectionPageFragment = (SectionPageFragment) currentFragment;
                        sectionPageFragment.refreshList();
                    }
                }
                finish();
            } else {
                showLoginDialog();
            }

        } else if (menuID == R.id.nav_review) {
            DialogFragment reviewDialog = ReviewDialog.newInstance();
            reviewDialog.show(getSupportFragmentManager(), "dialog");

        } else if (menuID == R.id.nav_orders) {
            if (facebookManager.checkLoginStatus()) {
                fragment = new ListOrdersFragment();
            } else {
                showLoginDialog();
            }
        }else if(menuID == R.id.nav_bronz){
            fragment = new ShopFragment();
        }else if(menuID == R.id.nav_user_network){
            fragment = new MyCircleContactsFragment();
        }else if(menuID == R.id.nav_bronz_products){
            ShopProductsFragment shopProductsFragment = new ShopProductsFragment();
            shopProductsFragment.setArguments(arguments);
            fragment = shopProductsFragment;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

            if (fragment instanceof SectionPageFragment) {
                if (sharedProductInvoked) {
                    if (sharedProduct == null) return;
                    Bundle bundle = new Bundle();
                    bundle.putString("PRODUCT", new Gson().toJson(sharedProduct));
                    bundle.putInt("KEYWORD_ID", sharedKeywordID);
                    fragment.setArguments(bundle);
                    sharedProductInvoked = false;
                }
                transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                transaction.replace(R.id.content_frame, fragment);
            } else {
                transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                transaction.add(R.id.content_frame, fragment).addToBackStack(null);
            }

            transaction.commit();

            if (fragment instanceof SectionPageFragment) {
                getSupportFragmentManager().popBackStack(null,
                        FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        }
    }

    private void handleGlobalsData(GlobalsClient.Response response) {
        SharedStorageHelper helper = new SharedStorageHelper(this);
        helper.setCODLimit(response.cod.cod_upper_limit, response.cod.cod_lower_limit);

        try {
            PackageInfo pInfo = activity.getPackageManager().getPackageInfo(
                    activity.getPackageName(), PackageManager.GET_META_DATA);
            String appVersionCode = String.valueOf(pInfo.versionCode);

            if (Integer.valueOf(appVersionCode) < Integer.valueOf(response.app.major_version)) {
//                DialogFragment updateDialog = UpdateDialog.newInstance();
//                updateDialog.show(getSupportFragmentManager(), "dialog");
            }
        } catch (Exception ignored) {}
    }

    public void setMenuBrewingCount(boolean increment) {
        if (increment)
            brewingProductCount++;

        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu menu = navigationView.getMenu();
        MenuItem myEspresso = menu.findItem(R.id.nav_pot);
        View actionView = myEspresso.getActionView();
        TextView number = actionView.findViewById(R.id.number);
        number.setText(String.valueOf(brewingProductCount));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null) {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        Log.d("REQUEST_CALLBACK","MainActivity");

         if (clientCode == ClientCodes.GET_QUOTE && response != null) {
            List<Quote> quotes = (List<Quote>) response;
            if (!quotes.isEmpty()) {
                quote = "\"" + quotes.get(0).quote + "\"";
                quoteView.setText(quote);
            }

        } else if (clientCode == ClientCodes.GLOBALS_CLIENT && response != null) {
            GlobalsClient.Response globalsData = (GlobalsClient.Response) response;
            handleGlobalsData(globalsData);

        } else if (clientCode == ClientCodes.GET_PRODUCT_OPTIONS && response != null) {
            GetProductOptionDetailsClient.Response details = (GetProductOptionDetailsClient.Response) response;
            sharedProduct = new Product();
            sharedProduct.name = details.name;
            sharedProduct.popup_images = details.images;
            sharedProduct.id_product_prestashop = sharedPrestashopID;
            sharedProduct.id_product = "0";
            sharedProduct.price = details.price;
            switchFragment(R.id.nav_home, true,null);

        } else if (clientCode == ClientCodes.GET_COUNT && response != null) {
            GetCountClient.Response countResponse = (GetCountClient.Response) response;
            brewingProductCount = countResponse.espresso_brewing_count;
            setMenuBrewingCount(false);

        } else if (clientCode == ClientCodes.GET_ACTIVE_KEYWORD && response != null) {
            ActiveKeywordClient.Response keywordResponse = (ActiveKeywordClient.Response) response;
            quotes = keywordResponse.quotes;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == ClientCodes.REQUEST_PERMISSIONS) {
            SharedStorageHelper helper = new SharedStorageHelper(this);

            if (grantResults.length > 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                helper.setStorageAccessPermitted(true);

            } else {
                helper.setStorageAccessPermitted(false);
            }
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        finish();
    }

    @Override
    public void onLoginClicked() {
        initFacebookLogin();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new SharedStorage(this).showPreferences("onDestroy");
    }

    public void clearJWT(){
        new SharedStorage(this).setValue(JWT,null);
    }

}
