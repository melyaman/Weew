package preferences.android.eurecom.fr.weew3;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import preferences.android.eurecom.fr.weew3.helper.SQLiteHandler;
import preferences.android.eurecom.fr.weew3.helper.SessionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SQLiteHandler db;
    private SessionManager session;
    public JSONArray EventList = new JSONArray();

    TextView userName;
    ImageView userImage;
    NavigationView navigationView = null;

    Toolbar toolbar = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());



        // session manager
        session = new SessionManager(getApplicationContext());

        setContentView(R.layout.activity_main);
        // set the fragment when app start to Events ( Or map ?? ) fragment
        Events events_fragment = new Events();
        android.support.v4.app.FragmentTransaction fragmentTransaction =
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container,events_fragment);
        fragmentTransaction.commit();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();





        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);

        SharedPreferences prefs;
        prefs = this.getSharedPreferences("WeeWLogin", Context.MODE_PRIVATE);
        String previouslyEncodedImage = prefs.getString("image_data", "");
        //Log.i("show: ",previouslyEncodedImage);
        if( !previouslyEncodedImage.equalsIgnoreCase("") ){
            byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
            Log.i("show here : ",bitmap.toString());
            HashMap<String, String> user = session.getUserDetails();
            userImage = (CircleImageView)  header.findViewById(R.id.profile_image);
            userName = (TextView) header.findViewById(R.id.email);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            userImage.setImageDrawable(d);
            userName.setText(user.get(SessionManager.KEY_EMAIL));
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            logoutUser();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_events) {
            // set the fragment when app start to Events ( Or map ?? ) fragment
            Events fragment = new Events();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        } else if (id == R.id.nav_createEvent) {
            // set the fragment when app start to Events ( Or map ?? ) fragment
            Create fragment = new Create();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_map) {
            Map fragment = new Map();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
            try {
                System.out.println("This is what I hope to have: "+ EventList.getJSONObject(0).getString("event_type"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else if (id == R.id.nav_editProfile) {
            EditProfile fragment = new EditProfile();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_about_us) {
//            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
//            startActivity(intent);

        } else if (id == R.id.nav_policy ){
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void logoutUser() {
        session.setLogin(false, "");

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, Login.class);
        startActivity(intent);
        finish();
    }

}
