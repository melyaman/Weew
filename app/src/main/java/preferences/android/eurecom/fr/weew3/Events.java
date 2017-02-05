package preferences.android.eurecom.fr.weew3;


import android.app.ProgressDialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import preferences.android.eurecom.fr.weew3.app.AppConfig;
import preferences.android.eurecom.fr.weew3.app.AppController;
import preferences.android.eurecom.fr.weew3.app.CustomAdapter;
import preferences.android.eurecom.fr.weew3.app.ListModel;

import static com.google.android.gms.wearable.DataMap.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class Events extends Fragment  {
    private ProgressDialog pDialog;
    public MainActivity homeActivity;
    int test = 0;
    ListView list;
    CustomAdapter adapter;
    public  Events CustomListView = null;
    //JSONArray eventsListJson = new JSONArray();
    public ArrayList<ListModel> CustomListViewValuesArr = new ArrayList<ListModel>();

    public Events() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_events, container, false);

        // Progress dialog
        pDialog = new ProgressDialog(rootView.getContext());
        pDialog.setCancelable(false);

        getAllEvents("allEvents") ;

        return rootView;
    }

    /**
     * Function to store user in MySQL database will post params(tag, name,
     * email, password) to register url
     * */



    private void getAllEvents(final String getAllEvents) {
        // Tag used to cancel the request
        String tag_string_req = "req_get_all_events_json";

        pDialog.setMessage("Getting Event ...");
        showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CREATE_EVENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Get Events Response: " + response.toString());
                hideDialog();

                try {
                    JSONArray jObj = new JSONArray(response);
                    boolean error = jObj.length()<1;
                    if (!error) {
                        setListData(jObj);
                        Resources res = getResources();
                        list = ( ListView )getActivity().findViewById( R.id.list );  // List defined in XML ( See Below )

                        /**************** Create Custom Adapter *********/
                        adapter=new CustomAdapter( getActivity(), CustomListViewValuesArr,res );
                        list.setAdapter( adapter );

                        homeActivity = (MainActivity) getActivity();
                        //Below is where you get a variable from the main activity
                        homeActivity.EventList = jObj;
                        Toast.makeText(getActivity().getApplicationContext(), "Events successfully fetched.", Toast.LENGTH_LONG).show();

                    } else {

                        // Error occurred in submitting. Get the error
                        // message
                        String errorMsg = jObj.getString(0);
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
                Log.e(TAG, "Event fetch Error: " + error.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to add event url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("allEvents", getAllEvents);
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

    /****** Function to set data in ArrayList *************/
    public void setListData( JSONArray eventsListJson) throws JSONException {

        for (int i = 0; i < eventsListJson.length(); i++) {
            final ListModel sched = new ListModel();
            JSONObject rec = eventsListJson.getJSONObject(i);
            /******* Firstly take data in model object ******/

            sched.setEvent_type(rec.getString("event_type"));
            sched.setTime_begin(rec.getString("time_begin"));
            sched.setTime_end(rec.getString("time_end"));
            sched.setEvent_date(rec.getString("event_date"));

            /******** Take Model Object in ArrayList **********/
            CustomListViewValuesArr.add( sched );
        }

    }


//    /*****************  This function used by adapter ****************/
    public void onItemClick(int mPosition)
    {
        //ListModel eventsValues = (ListModel) CustomListViewValuesArr.get(mPosition);

        System.out.println("Item selected"+ mPosition);
        // SHOW ALERT

        //Toast.makeText(getActivity(),"why",Toast.LENGTH_LONG).show();
    }
}
