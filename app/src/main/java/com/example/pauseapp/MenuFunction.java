package com.example.pauseapp;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class MenuFunction extends AppCompatActivity {
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    protected BottomNavigationView bottomNavigationView;
    protected ImageButton menuLateralButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void setupNavigation() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        menuLateralButton = findViewById(R.id.menuLateral);

        if (menuLateralButton != null) {
            menuLateralButton.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, LobbyActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else if (id == R.id.nav_friends) {
                    startActivity(new Intent(MenuFunction.this,FriendsActivity.class));
                    return true;
                } else if (id == R.id.nav_configuration){
                    startActivity(new Intent(MenuFunction.this,ConfigurationActivity.class));
                    return true;
                } else if (id == R.id.nav_out){
                    LogoutConfirmationFragment logoutDialog = new LogoutConfirmationFragment();
                    logoutDialog.show(getSupportFragmentManager(), "logoutDialog");
                }
                drawerLayout.closeDrawers();
                return true;
            });
        }

        if (bottomNavigationView != null) {
            bottomNavigationView.setOnItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(MenuFunction.this, LobbyActivity.class));
                    return true;
                } else if (id == R.id.nav_actividades) {
                    startActivity(new Intent(MenuFunction.this, ActivitiesListActivity.class));
                    return true;
                } else if (id == R.id.nav_perfil) {
                    startActivity(new Intent(MenuFunction.this, ProfileActivity.class));
                    return true;
                }
                return false;
            });
        }
    }

    protected void setupNavigationNoBottom() {
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        menuLateralButton = findViewById(R.id.menuLateral);

        if (menuLateralButton != null) {
            menuLateralButton.setOnClickListener(v -> drawerLayout.openDrawer(navigationView));
        }

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(item -> {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(this, LobbyActivity.class));
                } else if (id == R.id.nav_profile) {
                    startActivity(new Intent(this, ProfileActivity.class));
                } else if (id == R.id.nav_friends) {
                    startActivity(new Intent(MenuFunction.this,FriendsActivity.class));
                    return true;
                } else if (id == R.id.nav_configuration){
                    startActivity(new Intent(MenuFunction.this,ConfigurationActivity.class));
                    return true;
                } else if (id == R.id.nav_out){
                    LogoutConfirmationFragment logoutDialog = new LogoutConfirmationFragment();
                    logoutDialog.show(getSupportFragmentManager(), "logoutDialog");
                }
                drawerLayout.closeDrawers();
                return true;
            });
        }
    }
}
