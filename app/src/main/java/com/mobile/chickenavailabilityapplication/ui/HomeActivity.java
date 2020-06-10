package com.mobile.chickenavailabilityapplication.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ActionMenuView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItemContainer;
import com.mobile.chickenavailabilityapplication.datamodel.Customer;
import com.mobile.chickenavailabilityapplication.datamodel.ProfileKeyValue;
import com.mobile.chickenavailabilityapplication.dummy.DummyContent;

import java.util.HashSet;
import java.util.Set;

public class HomeActivity extends ParentActivity implements DashboardMenuItemFragment.OnListFragmentInteractionListener,
                                                            OrdersFragment.OnListFragmentInteractionListener,
                                                            CartFragment.OnListFragmentInteractionListener,
                                                            ItemDescriptionFragment.OnFragmentInteractionListener,
                                                            ProfileItemFragment.OnListFragmentInteractionListener,
                                                            ReviewCartItemFragment.OnListFragmentInteractionListener
{

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    AppBarConfiguration appBarConfiguration;
    View notificationsBadge=null;
    BottomNavigationMenuView mBottomNavigationMenuView;
    private Boolean mNewAccount=false;
    Menu profileMenu;
    private ActionMenuView amvMenu;
    float deviceWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        /*DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceWidth = displayMetrics.widthPixels;*/
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        //DisplayMetrics outMetrics = new DisplayMetrics ();
        //display.getMetrics(outMetrics);

       // float density  = getResources().getDisplayMetrics().density;
        //float dpHeight = outMetrics.heightPixels / density;
       // deviceWidth  = outMetrics.widthPixels ;
        deviceWidth  = size.x ;
        mNewAccount = preferences.getBoolean("NewAccount",false);
        setUpNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void setUpNavigation(){
        bottomNavigationView =  findViewById(R.id.bottomNavView);
        toolbar =   findViewById(R.id.toolbar);
        /*amvMenu.setOnMenuItemClickListener(new ActionMenuView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });*/
        setSupportActionBar(toolbar);

        Set<Integer> topLevelDestinations = new HashSet<>();
        topLevelDestinations.add(R.id.dashboardMenuItemFragment);
        topLevelDestinations.add(R.id.ordersFragment);
        topLevelDestinations.add(R.id.cartFragment);
        NavController navController = Navigation.findNavController(this, R.id.fragNavHost);
        NavigationUI.setupWithNavController(bottomNavigationView,navController);
        appBarConfiguration = new AppBarConfiguration.Builder(topLevelDestinations)
                .build();

        NavigationUI.setupWithNavController(toolbar,navController,appBarConfiguration);
        navController.addOnDestinationChangedListener(mOnDestinationChangedListener);
    }

    public void showBottomNavigation()
    {
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    public void hideBottomNavigation()
    {
        bottomNavigationView.setVisibility(View.GONE);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }
    //MenuList Item Interaction
    @Override
    public void onListFragmentInteraction(String productID) {
        NavController navController = Navigation.findNavController(this, R.id.fragNavHost);
        Bundle bundle = new Bundle();
        bundle.putString("productID", productID);
        navController.navigate(R.id.action_menu_to_description, bundle);
    }
    @Override
    public void onProfileListFragmentInteraction(ProfileKeyValue item) {

        NavController navController = Navigation.findNavController(this, R.id.fragNavHost);
        switch (item.Key){
            case "Billing Address":
            case "Shipping Address":
                navController.navigate(R.id.action_profileItemFragment_to_updateAddressFragment);
            break;
            case "First Name":
            case "Last Name":
                navController.navigate(R.id.action_profileItemFragment_to_updateNameFragment);
                break;
            case "Password":
                navController.navigate(R.id.action_profileItemFragment_to_updatePasswordFragment);
            default:
                break;
        }
    }

    public void addNotificationBadge(int count,int RESID) {
        BadgeDrawable badge  = bottomNavigationView.getOrCreateBadge(
                RESID);
        badge.setNumber(count);
        badge.setVisible(true);
    }
    public void removeNotificationBadge(int RESID) {
        bottomNavigationView.removeBadge(RESID);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_menu, menu);
        /*MenuItem searchItem = profileMenu.findItem(R.id.action_profile);
        ActionMenuView searchView =
                (ActionMenuView) searchItem.getActionView();
        searchView.setGravity(Gravity.END);*/
        profileMenu=menu;
        if(profileMenu != null) {
            profileMenu.findItem(R.id.action_profile).setVisible(false);
            //profileMenu.findItem(R.id.action_profile).getActionView().setForegroundGravity(Gravity.END);
            profileMenu.findItem(R.id.action_profile).setEnabled(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.action_profile){
            NavController navController = Navigation.findNavController(this, R.id.fragNavHost);
            Bundle bundle = new Bundle();
            bundle.putString("customerID", Customer.getInstance().customerId);
            navController.navigate(R.id.action_ordersFragment_to_profileItemFragment, bundle);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private final NavController.OnDestinationChangedListener mOnDestinationChangedListener
            = new NavController.OnDestinationChangedListener(){

        @Override
        public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
            if(profileMenu != null) {
                profileMenu.findItem(R.id.action_profile).setVisible(false);
                profileMenu.findItem(R.id.action_profile).setEnabled(false);
            }
            ((AppBarLayout.LayoutParams)toolbar.getLayoutParams()).gravity= Gravity.CENTER;
            //int dp =deviceWidth/(int)getResources().getDisplayMetrics().density ;
            //toolbar.setTitleMarginStart((int)deviceWidth/2);
            toolbar.setContentInsetsRelative((((int)deviceWidth/2)-50),0);
            //toolbar.setContentInsetsAbsolute(150,0);
            //toolbar.setLayoutParams(Lay);
            showBottomNavigation();
            if(CartItemContainer.readCartItemContainer().cartItems.size()!=0)
                addNotificationBadge(CartItemContainer.readCartItemContainer().cartItems.size(),R.id.cartFragment);
            else
                removeNotificationBadge(R.id.cartFragment);
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null)
            {
                actionBar.setDisplayHomeAsUpEnabled(false); //Set this to true if selecting "home" returns up by a single level in your UI rather than back to the top level or front page.
            }
            switch(destination.getId()){

                case R.id.ordersFragment:
                    if(profileMenu != null) {
                        //((AppBarLayout.LayoutParams)).gravity= Gravity.END;
                        profileMenu.findItem(R.id.action_profile).setVisible(true);
                        profileMenu.findItem(R.id.action_profile).setEnabled(true);
                    }                        break;

                case R.id.itemDescriptionFragment:
                case R.id.profileItemFragment:
                case R.id.reviewCartItemFragment:
                case R.id.updateAddressFragment:
                case R.id.updateNameFragment:
                case R.id.updatePasswordFragment:
                    ((AppBarLayout.LayoutParams)toolbar.getLayoutParams()).gravity= Gravity.START;
                    toolbar.setContentInsetsRelative(0,0);

                    // toolbar.setContentInsetsAbsolute(0,0);
                    if (actionBar != null)
                    {
                        actionBar.setDisplayHomeAsUpEnabled(true); //Set this to true if selecting "home" returns up by a single level in your UI rather than back to the top level or front page.
                        actionBar.setHomeAsUpIndicator(R.drawable.ic_action_head_back); // set a custom icon for the default home button
                    }
                    hideBottomNavigation();
                    break;

                default:
                    showBottomNavigation();
            }

        }
    };


    @Override
    public void onReviewCartListFragmentInteraction(String category) {

    }
}
