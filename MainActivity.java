/*
 * Created by Signals Buddy on Aug 29, 2017.
 * Copyright (c) Aug 29, 2017. All right reserved.
 *
 * Last modified 4/24/18 3:13 PM
 */

package com.signalsbuddy.signalsbuddy;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.app.signalsbuddy.R;
import com.google.firebase.messaging.FirebaseMessaging;
import com.signalsbuddy.signalsbuddy.PagerAdapter.ViewPagerAdapter;
import com.signalsbuddy.signalsbuddy.ServiceHandler.GetUserProfile;
import com.signalsbuddy.signalsbuddy.UserActivity.HistoryActivity;
import com.signalsbuddy.signalsbuddy.UserActivity.LoginActivity;
import com.signalsbuddy.signalsbuddy.UserActivity.NewsActivity;
import com.signalsbuddy.signalsbuddy.UserActivity.OnboardingActivity;
import com.signalsbuddy.signalsbuddy.UserActivity.ProfileActivity;
import com.signalsbuddy.signalsbuddy.UserActivity.SettingActivity;

public class MainActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private Toolbar mToolbar;
    private TabLayout mTabLayout;
    private ViewPagerAdapter mViewPagerAdapter;
    private boolean pendingIntroAnimation;
    SharedPreferences sharedPreferences,prefUserProfile,prefNotif;
    SharedPreferences.Editor editorNotif,editorprofile;
    public final String pref_name = "SESSION_PREF";
    public final String is_login = "LOGIN_SESSION";
    boolean statusProfile ;
    int mode = 0;

    static{
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefNotif = getSharedPreferences("NOTIF",0);
        editorNotif = prefNotif.edit();
        editorNotif.clear();
        editorNotif.apply();
        sharedPreferences = getSharedPreferences(pref_name, mode);
        prefUserProfile = getSharedPreferences(getString(R.string.SHARED_PROFILE),mode);
        mTabLayout = (TabLayout) findViewById(R.id.tab2);
        mViewPager = (ViewPager) findViewById(R.id.pager);
        statusProfile = prefUserProfile.getBoolean("STATUS_PROFILE",false);

        if(!statusProfile){
            Intent in = new Intent(MainActivity.this, GetUserProfile.class);
            startService(in);

        }

        setViewPager();

    }

    private void setViewPager() {

        mTabLayout.addTab(mTabLayout.newTab().setText("Active"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Cancel"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Settled"));
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);

        mViewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        int limit = (mViewPagerAdapter.getCount() > 1 ? mViewPagerAdapter.getCount() - 1 : 1);
        mViewPager.setAdapter(mViewPagerAdapter);
        mViewPager.setOffscreenPageLimit(limit);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(mTabLayout));

        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());


            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }


    boolean doubleBackToExitPressedOnce = false;
    Handler handler = new Handler();
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
          super.onBackPressed();
            return;
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        if (pendingIntroAnimation){
            pendingIntroAnimation = false;
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_settings:
                Intent inten = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(inten);
                return true;

            case R.id.action_profile:
                Intent in = new Intent(getApplicationContext(), ProfileActivity.class);
                startActivity(in);
                return true;

            case R.id.action_info:
                Intent intent4= new Intent(getApplicationContext(), OnboardingActivity.class);
                intent4.putExtra("FROMMENU",true);
                startActivity(intent4);
                return true;

            case R.id.action_news:
                Intent intent = new Intent(getApplicationContext(), NewsActivity.class);
                startActivity(intent);
                return true;

            case R.id.action_history:
                Intent intent2 = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(intent2);
                return true;

            case R.id.action_logout:
                logout();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


    public void logout(){

        String e = getString(R.string.exit);
        AlertDialog.Builder ad = new AlertDialog.Builder(this);
        ad.setTitle(getString(R.string.warning))
                .setMessage(e)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean(is_login, false);
                        editor.apply();
                        editorprofile = prefUserProfile.edit();
                        editorprofile.putBoolean("STATUS_PROFILE",false);
                        editorprofile.apply();
                        Intent in = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(in);
                        finish();
                        FirebaseMessaging.getInstance().unsubscribeFromTopic("SIGNALS");
//                        Paper.book().delete("CACHEACTIVE");
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });


        AlertDialog dialog = ad.create();
        dialog.show();

    }




}
