package com.mobile.chickenavailabilityapplication.ui;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItem;
import com.mobile.chickenavailabilityapplication.datamodel.CartItemContainer;
import com.mobile.chickenavailabilityapplication.datamodel.MenuItem;
import com.mobile.chickenavailabilityapplication.dummy.DummyContent;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends ParentActivity implements DashboardMenuItemFragment.OnListFragmentInteractionListener,
                                                            OrdersFragment.OnListFragmentInteractionListener,
                                                            CartFragment.OnListFragmentInteractionListener,
                                                            ItemDescriptionFragment.OnFragmentInteractionListener  {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    AppBarConfiguration appBarConfiguration;
    View notificationsBadge=null;
    BottomNavigationMenuView mBottomNavigationMenuView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpNavigation();
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void setUpNavigation(){
        bottomNavigationView =  findViewById(R.id.bottomNavView);
        toolbar =   findViewById(R.id.toolbar);
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
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull NavController controller, @NonNull NavDestination destination, @Nullable Bundle arguments) {
                ((AppBarLayout.LayoutParams)toolbar.getLayoutParams()).gravity= Gravity.CENTER;
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
                    case R.id.dashboardMenuItemFragment:
                        showBottomNavigation();
                        /*if(arguments != null) {
                            if (arguments.getBoolean("itemadded")) {
                                Toast.makeText(getApplicationContext(), "ItemAdded", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"backpressed",Toast.LENGTH_SHORT).show();
                            }
                        }*/
                        break;
                    case R.id.itemDescriptionFragment:
                        ((AppBarLayout.LayoutParams)toolbar.getLayoutParams()).gravity= Gravity.START;
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
        });

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

    /*private View getBadge(){
        if(notificationsBadge != null){
            return notificationsBadge;
        }
        mBottomNavigationMenuView=(BottomNavigationMenuView) bottomNavigationView.getChildAt(0) ;
        View v = mBottomNavigationMenuView.getChildAt(2);
        BottomNavigationItemView itemView = (BottomNavigationItemView) v;
        notificationsBadge= LayoutInflater.from(this).inflate(R.layout.notification_badge_layout,itemView,false);
        return notificationsBadge;
    }

    private void addBadge(int count) {
        getBadge();
        TextView textView = notificationsBadge.findViewById(R.id.notifications_badge);
        textView.setText(String.valueOf(count));
        bottomNavigationView.addView(notificationsBadge);
    }

    private void removeBadge() {

        bottomNavigationView.removeView(notificationsBadge);
    }*/

    private void addNotificationBadge(int count,int RESID) {
        BadgeDrawable badge  = bottomNavigationView.getOrCreateBadge(
                RESID);
        badge.setNumber(count);
        badge.setVisible(true);
    }
    private void removeNotificationBadge(int RESID) {
     bottomNavigationView.removeBadge(RESID);
    }
}
