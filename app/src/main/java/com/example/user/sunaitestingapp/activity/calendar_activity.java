package com.example.user.sunaitestingapp.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.sunaitestingapp.R;
import com.example.user.sunaitestingapp.common.GlobalClass;
import com.example.user.sunaitestingapp.fragments.Calender_Fragment;
import com.example.user.sunaitestingapp.fragments.addvacation_fragment;
import com.example.user.sunaitestingapp.fragments.backhome_fragment;
import com.example.user.sunaitestingapp.fragments.delivery_change_fragment;
import com.example.user.sunaitestingapp.fragments.order_confirmation;
import com.example.user.sunaitestingapp.fragments.subscription_fragment;
import com.example.user.sunaitestingapp.fragments.vacation_fragment;
import com.example.user.sunaitestingapp.fragments.wallet_fragment;

public class calendar_activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener ,Calender_Fragment.OnFragmentInteractionListener,delivery_change_fragment.OnFragmentInteractionListener,
        wallet_fragment.OnFragmentInteractionListener,vacation_fragment.OnFragmentInteractionListener,subscription_fragment.OnFragmentInteractionListener,backhome_fragment.OnFragmentInteractionListener,
        order_confirmation.OnFragmentInteractionListener,addvacation_fragment.OnFragmentInteractionListener{
       TextView textView;
       Toolbar toolbar;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ImageView addvacation,wallet_img;
    TextView wallet_val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar_activity2);
        findViewId();
        toolbar.setTitle("Home");
        setSupportActionBar(toolbar);
        final GlobalClass globalVariable = (GlobalClass) getApplicationContext();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        textView = (TextView)navigationView.getHeaderView(0). findViewById(R.id.textView);

        textView.setText(" "+globalVariable.username);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,new Calender_Fragment());
        fragmentTransaction.commit();

       /* wallet_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),wallet_activity.class);
                startActivity(intent);
            }
        });*/
    }

    public void findViewId() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        TextView wallet_val;

      /*  wallet_img = (ImageView)findViewById(R.id.wallet_img);
        wallet_val =(TextView)findViewById(R.id.wallet_val);*/
    }
    boolean  doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

               if (doubleBackToExitPressedOnce) {
                    this.finish();
                    return;
                }
                calendar_activity.this.doubleBackToExitPressedOnce = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce=false;
                    }
                }, 2000);



        }

    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     getMenuInflater().inflate(R.menu.calendar_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }*/

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment f = null;
        int id = item.getItemId();


        if (id == R.id.home) {
         //   getSupportFragmentManager().beginTransaction().remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container));
            f =  new Calender_Fragment();
            toolbar.setTitle("Home");
          //  addvacation.setVisibility(View.GONE);

        } else if (id == R.id.subscriptions) {
   /*   f=new subscription_fragment();
     toolbar.setTitle("Subscriptions");*/

            Intent intent = new Intent(getApplicationContext(),productsublist_activity.class);
            startActivity(intent);

        } else if (id == R.id.wallet) {
            Intent intent = new Intent(getApplicationContext(),wallet_activity.class);
            startActivity(intent);
         /*  f=new wallet_fragment();
            toolbar.setTitle("Wallet");*/
        //    addvacation.setVisibility(View.GONE);

        } else if (id == R.id.vacations) {

            Intent intent = new Intent(getApplicationContext(),vacation_activity.class);
            startActivity(intent);
          /*  f=new vacation_fragment();
            toolbar.setTitle("Vacations");*/
         //   addvacation.setVisibility(View.VISIBLE);

        } else if (id == R.id.view_bills) {
          //  addvacation.setVisibility(View.GONE);

        } else if (id == R.id.products) {
          //  addvacation.setVisibility(View.GONE);
            Intent intent = new Intent(getApplicationContext(),productlist_activity.class);
            startActivity(intent);


        }
        else if (id == R.id.offers) {
          //  addvacation.setVisibility(View.GONE);

        } else if (id == R.id.contactus) {
          //  addvacation.setVisibility(View.GONE);

        } else if (id == R.id.logout) {
            final GlobalClass globalVariable = (GlobalClass) getApplicationContext();
            globalVariable.getSharedPreferences().edit()
                    .putString("userName",    "")
                    .putString("password","")
                    .putBoolean("is_loggedIn", false)
                    .apply();
            Intent intent = new Intent(getApplicationContext(), login_activity.class);
            startActivity(intent);
            finish();
        }
        if (f != null) {

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, f)
                    .commit();


        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(Gravity.START))
         //   drawer.closeDrawers();
       drawer.closeDrawer(GravityCompat.START);
     //   setSupportActionBar(toolbar);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
