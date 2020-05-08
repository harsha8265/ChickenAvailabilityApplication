package com.mobile.chickenavailabilityapplication.ui;

import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.dummy.DummyContent;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class HomeActivity extends ParentActivity implements DashboardMenuItemFragment.OnListFragmentInteractionListener,OrdersFragment.OnListFragmentInteractionListener,CartFragment.OnFragmentInteractionListener,ItemDescriptionFragment.OnFragmentInteractionListener  {

    BottomNavigationView bottomNavigationView;
    Toolbar toolbar;
    AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setUpNavigation();
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

                switch(destination.getId()){
                    case R.id.dashboardMenuItemFragment:
                        showBottomNavigation();
                        if(arguments != null){
                            if(arguments.getBoolean("itemadded")){
                                Toast.makeText(getApplicationContext(),"ItemAdded",Toast.LENGTH_SHORT).show();
                            }
                        }
                        break;
                    case R.id.itemDescriptionFragment:
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
    public void onListFragmentInteraction(com.mobile.chickenavailabilityapplication.datamodel.MenuItem item) {
        NavController navController = Navigation.findNavController(this, R.id.fragNavHost);
        Bundle bundle = new Bundle();
        bundle.putSerializable("menuitem", item);
        navController.navigate(R.id.action_menu_to_description, bundle);
    }
}
