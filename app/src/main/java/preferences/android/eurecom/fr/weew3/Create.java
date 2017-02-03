package preferences.android.eurecom.fr.weew3;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

import preferences.android.eurecom.fr.weew3.app.AppConfig;
import preferences.android.eurecom.fr.weew3.app.AppController;
import preferences.android.eurecom.fr.weew3.helper.SQLiteEventHandler;
import preferences.android.eurecom.fr.weew3.helper.SessionManager;

import static android.app.Activity.RESULT_OK;
import static com.google.android.gms.wearable.DataMap.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Create extends Fragment {
    ///// edit texts declaration
    private EditText edittext;
    private EditText startText;
    private EditText endText;
    private EditText locationText;
    private EditText descriptionText;
    private Button btnSubmitEvent;
    int PLACE_PICKER_REQUEST = 1;
    private ProgressDialog pDialog;
    private SQLiteEventHandler db;
    private SessionManager session;



    public Create() {
        // Required empty public constructor
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_create, container, false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.event_choices, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        final Spinner spinner = (Spinner) rootView.findViewById(R.id.eventTypeSpinner);
        spinner.setAdapter(adapter);


        startText = (EditText) rootView.findViewById(R.id.eventStartText);
        endText = (EditText) rootView.findViewById(R.id.eventEndText);
        edittext = (EditText) rootView.findViewById(R.id.eventDateText);
        descriptionText = (EditText) rootView.findViewById(R.id.descriptionText);
        locationText  = (EditText) rootView.findViewById(R.id.locationText);
        btnSubmitEvent = (Button) rootView.findViewById(R.id.submitEvent);
        // Progress dialog
        pDialog = new ProgressDialog(rootView.getContext());
        pDialog.setCancelable(false);


        // Session manager
        session = new SessionManager(getActivity().getApplicationContext());


        // SQLite database handler
        db = new SQLiteEventHandler(getActivity().getApplicationContext());

        final Calendar myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

            private void updateLabel() {

                String myFormat = "yyyy-MM-dd"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                edittext.setText(sdf.format(myCalendar.getTime()));

            }
        };
        edittext.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(rootView.getContext(), date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        startText.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    Calendar mcurrentTime = Calendar.getInstance();
                    int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                    int minute = mcurrentTime.get(Calendar.MINUTE);
                    TimePickerDialog mTimePicker;
                    mTimePicker = new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                            startText.setText( "" + selectedHour + ":" + selectedMinute);
                        }
                    }, hour, minute, true);
                    mTimePicker.setTitle("Select Time");
                    mTimePicker.show();
                }
            });

        endText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(rootView.getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endText.setText( "" + selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
                System.out.println(spinner.getSelectedItem().toString());
                System.out.println(edittext.getText());
                System.out.println(startText.getText());
                System.out.println(endText.getText());
            }
        });
        // place picker for location
        locationText.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                Intent intent;
                try {
                    intent = builder.build( getActivity());
                    startActivityForResult(intent,PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }

            }
        });

        // Submit Event Button Click event
        btnSubmitEvent.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // get user data from session
                HashMap<String, String> user = session.getUserDetails();

                // email from preferences
                String email = user.get(SessionManager.KEY_EMAIL);

                //event_date, event_type, loc_lat, loc_long, picture, time_begin, time_end, description
                String event_type = spinner.getSelectedItem().toString().trim();
                String event_date = edittext.getText().toString().trim();
                String time_begin = startText.getText().toString().trim();
                String time_end = endText.getText().toString().trim();
                String description = descriptionText.getText().toString().trim();
                String event_location = locationText.getText().toString().trim();
                Float loc_lat =  Float.parseFloat(event_location.split(",")[0]);
                Float loc_long =  Float.parseFloat(event_location.split(",")[1]);
                String picture = "000000000000000001";
                System.out.println(email + event_type + event_date + time_begin + time_end + description + event_location + picture);

                if (!event_type.isEmpty() && !event_date.isEmpty() && !time_begin.isEmpty()) {
                    addEvent(email, event_date, event_type, loc_lat, loc_long, picture, time_begin, time_end, description);
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please enter the event details!", Toast.LENGTH_LONG)
                            .show();
                }
            }
        });

        return rootView;
    }

    public void onActivityResult(int requestCode,int resultCode,Intent data){

        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(getActivity().getApplicationContext(), data);
                System.out.println("------***************---------- "+place.getAddress().toString()+ (place.getLatLng()).getClass());
                //String toastMsg = String.format("Place: %s", place.getName());
                String toastMsg = Double.toString(place.getLatLng().latitude)+ ","+ Double.toString(place.getLatLng().longitude)  ;
                Toast.makeText(getActivity(), toastMsg, Toast.LENGTH_LONG).show();
                locationText.setText(toastMsg);
            }
        }
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */
    private void addEvent(final String email, final String event_date, final String event_type, final float loc_lat,
                          final float loc_long, final String picture, final String time_begin, final String time_end,
                          final String description) {
        // Tag used to cancel the request
        String tag_string_req = "req_add_event";

        pDialog.setMessage("Adding Event ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATE_EVENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Add Event Response: " + response.toString());
                hideDialog();

                try {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");
                    if (!error) {
                        System.out.println("why not enterinjg");
                        // Event successfully stored in MySQL
                        // Now store the event in sqlite


                        JSONObject event = jObj.getJSONObject("event");

                        String evid = event.getString("evid");
                        String email = event.getString("email");
                        String event_date = event.getString("event_date");
                        String event_type = event.getString("event_type");
                        float loc_lat = (float) event.getDouble("loc_lat");
                        float loc_long = (float) event.getDouble("loc_long");
                        String picture = event.getString("picture");
                        String time_begin = event.getString("time_begin");
                        String time_end = event.getString("time_end");
                        String description = event.getString("description");

                        // Inserting row in events table
                        db.addEvent(evid, email, event_date, event_type, loc_lat, loc_long, picture, time_begin, time_end, description);

                        Toast.makeText(getActivity().getApplicationContext(), "Event successfully submitted.", Toast.LENGTH_LONG).show();

                        // Launch event fragment
                        Events fragment = new Events();
                        android.support.v4.app.FragmentTransaction fragmentTransaction =
                                getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment_container,fragment);
                        fragmentTransaction.commit();

                    } else {

                        // Error occurred in submitting. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        Toast.makeText(getActivity().getApplicationContext(),
                                errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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
                params.put("email", email);
                params.put("event_date", event_date);
                params.put("event_type", event_type);
                params.put("loc_lat", Float.toString(loc_lat));
                params.put("loc_long", Float.toString(loc_long));
                params.put("picture", picture);
                params.put("time_begin", time_begin);
                params.put("time_end", time_end);
                params.put("description", description);

                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

}