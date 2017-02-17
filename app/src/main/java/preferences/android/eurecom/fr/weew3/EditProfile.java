package preferences.android.eurecom.fr.weew3;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.util.Calendar;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import android.app.ProgressDialog;
import android.content.Intent;

import de.hdodenhof.circleimageview.CircleImageView;
import preferences.android.eurecom.fr.weew3.app.AppConfig;
import preferences.android.eurecom.fr.weew3.app.AppController;
import preferences.android.eurecom.fr.weew3.helper.SQLiteHandler;
import preferences.android.eurecom.fr.weew3.helper.SessionManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.wearable.DataMap.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfile extends Fragment {

    private static final String TAG = EditProfile.class.getSimpleName();
    int RESULT_LOAD_IMAGE = 1;
    final String FILEPATH = "FilePath";
    Bitmap thumbnail = null;
    EditText new_username;
    EditText old_password;
    EditText new_password;
    ImageButton myImage;
    SharedPreferences prefs;
    TextView userName;
    CircleImageView userImage;
    private ProgressDialog pDialog;
    private SessionManager session;
    private Button btnSubmit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_edit_profile, container, false);

        contextOfApplication = getActivity().getApplicationContext();

        final View rView = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
        View header=navigationView.getHeaderView(0);


        new_username = (EditText) rView.findViewById(R.id.new_username);
        new_password = (EditText) rView.findViewById(R.id.new_password);
        old_password = (EditText) rView.findViewById(R.id.old_password);
        myImage = (ImageButton) rView.findViewById(R.id.myButton);

        userImage = (CircleImageView)  header.findViewById(R.id.profile_image);
        userName = (TextView) header.findViewById(R.id.email);

        btnSubmit = (Button) rView.findViewById(R.id.submit);
        session = new SessionManager(getActivity().getApplicationContext());
        pDialog = new ProgressDialog(rView.getContext());
        pDialog.setCancelable(false);

        myImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1);

                //myImage.setBackground(new BitmapDrawable(BitmapFactory.decodeFile(prefs.getString(FILEPATH, ""))));
                //myImage.setImageBitmap(thumbnail);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();

                // email from preferences
                String email = user.get(SessionManager.KEY_EMAIL);

                //event_date, event_type, loc_lat, loc_long, picture, time_begin, time_end, description
                String newUserName = new_username.getText().toString().trim();
                String oldPassword = old_password.getText().toString().trim();
                String newPassword = new_password.getText().toString().trim();

                updateUser(email, newUserName , oldPassword, newPassword);

            }
        });
        return rView;
    };



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Context applicationContext = this.getContextOfApplication();

            Cursor cursor = applicationContext.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            //prefs.edit().putString(FILEPATH, picturePath);
            thumbnail = (BitmapFactory.decodeFile(picturePath));
            Drawable d = new BitmapDrawable(getResources(), thumbnail);
            myImage.setImageBitmap(thumbnail);

            //userName.setText("this will not work");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();

            String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);

            prefs = getActivity().getSharedPreferences("WeeWLogin", Context.MODE_PRIVATE);
            SharedPreferences.Editor edit=prefs.edit();
            edit.putString("image_data",encodedImage);
            edit.commit();

            userImage.setImageDrawable(d);

            //thumbnail = decodeFile(picturePath);
        }


    }

    // Decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(String picturePath) {

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath,o);

            // The new size we want to scale to
            final int REQUIRED_SIZE=70;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while(o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            // Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeFile(picturePath , o2);
    }


    public static Context contextOfApplication;
    public static Context getContextOfApplication()
    {
        return contextOfApplication;
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void updateUser(final String email, final String newName, final String oldPassword , final String newPassword) {
        // Tag used to cancel the request
        String tag_string_req = "req_update_user";
        pDialog.setMessage("Updating user profile ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_USER, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Updating User Response: " + response.toString());
                hideDialog();

                Toast.makeText(getActivity().getApplicationContext(), "User successfully modified.", Toast.LENGTH_LONG).show();

                // Launch event fragment
                Events fragment = new Events();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();

//                try {
////                    JSONObject jObj = new JSONObject(response);
////                    boolean error = jObj.getBoolean("error");
//                    JSONObject jObj = new JSONObject(response);
//                    boolean error = jObj.getBoolean("error");
//                    if (!error) {
//                        // Event successfully stored in MySQL
//                        // Now store the event in sqlite
//
//                        System.out.println("Ïmin *****");
//                        //JSONObject user = jObj.getJSONObject("user");
//
//
//
//                    } else {
//
//                        // Error occurred in submitting. Get the error
//                        // message
//                        String errorMsg = jObj.getString("old password is wrong");
//                        Toast.makeText(getActivity().getApplicationContext(),
//                                errorMsg, Toast.LENGTH_LONG).show();
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Event Submission Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to add event url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("newName", newName);
                params.put("email", email);
                params.put("oldPassword", oldPassword);
                params.put("newPassword", newPassword);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}
