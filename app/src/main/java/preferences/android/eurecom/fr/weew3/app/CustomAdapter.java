package preferences.android.eurecom.fr.weew3.app;

/**
 * Created by aabdelli on 03/02/2017.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;

import preferences.android.eurecom.fr.weew3.Events;
import preferences.android.eurecom.fr.weew3.MainActivity;
import preferences.android.eurecom.fr.weew3.R;
import preferences.android.eurecom.fr.weew3.helper.SessionManager;

import static com.google.android.gms.wearable.DataMap.TAG;

/********* Adapter class extends with BaseAdapter and implements with OnClickListener ************/
public class CustomAdapter extends BaseAdapter implements View.OnClickListener {
    private ProgressDialog pDialog;
    /*********** Declare Used Variables *********/
    private Activity activity;
    private ArrayList data;
    private static LayoutInflater inflater=null;
    public Resources res;
    ListModel eventValues=null;
    int i=0;
    ImageView joion;
    private SessionManager session;

    /*************  CustomAdapter Constructor *****************/
    public CustomAdapter(Activity a, ArrayList d,Resources resLocal) {

        /********** Take passed values **********/
        activity = a;
        data=d;
        res = resLocal;

        /***********  Layout inflator to call external xml layout () ***********/
        inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    /******** What is the size of Passed Arraylist Size ************/
    public int getCount() {

        if(data.size()<=0)
            return 1;
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    /********* Create a holder Class to contain inflated xml file elements *********/
    public static class ViewHolder{

        public TextView text;
        public TextView text1;
        public TextView textWide;
        public ImageView image;
        public ImageView joinImage;
    }

    /****** Depends upon data size called for each row , Create each ListView row *****/
    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate tabitem.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.tabitem, null);

            /****** View Holder Object to contain tabitem.xml file elements ******/

            holder = new ViewHolder();
            holder.text = (TextView) vi.findViewById(R.id.text);
            holder.text1=(TextView)vi.findViewById(R.id.text1);
            holder.image=(ImageView) vi.findViewById(R.id.image);
            holder.joinImage=(ImageView)vi.findViewById(R.id.joinEvent);


            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else
            holder=(ViewHolder)vi.getTag();

        if(data.size()<=0)
        {
            holder.text.setText("No Data");

        }
        else
        {
            /***** Get each Model object from Arraylist ********/
            eventValues=null;
            eventValues = ( ListModel ) data.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.text.setText( eventValues.getEvent_type() );
            holder.text1.setText(eventValues.getEvent_date() + "\n" + eventValues.getEmail() );
            if (eventValues.getEvent_type().equals("NightLife") ) {
                holder.image.setImageResource(R.drawable.nightlife);
            };
            if (eventValues.getEvent_type().equals("Sports")) {
                holder.image.setImageResource(R.drawable.sports);
            };
            if (eventValues.getEvent_type().equals("Food")) {
                holder.image.setImageResource(R.drawable.food);
            };
            if (eventValues.getEvent_type().equals("Study Groups")) {
                holder.image.setImageResource(R.drawable.study);
            };
            if (eventValues.getEvent_type().equals("Travel")) {
                holder.image.setImageResource(R.drawable.travel);
            };
            if (eventValues.getEvent_type().equals("Others")) {
                holder.image.setImageResource(R.drawable.plus);
            };

//                    res.getIdentifier(
//                            "preferences.android.eurecom.fr.weew3.app:drawable:drawable/sports.png" //eventValues.getEvent_type()
//                            ,null,null));

            /******** Set Item Click Listner for LayoutInflater for each row *******/

            vi.setOnClickListener(new OnItemClickListener( position ));

            holder.joinImage.setOnClickListener(new OnItemJoinClickListener( position ));
        }
        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {

            Events fragment = new Events();

            eventValues = ( ListModel ) data.get( mPosition );

            fragment.onItemClick(mPosition);
            String[] eventMembers = eventValues.getEvent_members().toString().split(",");
            String membersString="";
            for (i=0;i<eventMembers.length;i++ ){
                membersString = membersString + eventMembers[i]+"\n";
            }
            Toast.makeText(activity.getApplicationContext(),
                    "Event Details:\n"+
                            "Event time begin:"+eventValues.getTime_begin().toString().trim()+"\n"+
                            "Event time end:"+eventValues.getTime_end().toString().trim()+"\n"+
                            "Event Description:"+eventValues.getDescription().toString().trim()+"\n"+
                            "Members:"+"\n"+membersString
                    , Toast.LENGTH_LONG).show();
        }
    }
    /********* Called when Item click in ListView ************/
    private class OnItemJoinClickListener  implements View.OnClickListener {
        private int mPosition;

        OnItemJoinClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            session = new SessionManager(activity.getApplicationContext());
            Events fragment = new Events();
            HashMap<String, String> user = session.getUserDetails();

            // email from preferences
            String email = user.get(SessionManager.KEY_EMAIL);
//            pDialog = new ProgressDialog(fragment.getContext());
//            pDialog.setCancelable(false);
            eventValues = ( ListModel ) data.get( mPosition );
            updateEvent(eventValues.getEvent_id().toString().trim(),email);
            Toast.makeText(activity.getApplicationContext(),
                    "Event joind successufully *__*"
                    , Toast.LENGTH_LONG).show();
//            try {
//                fragment.onItemJoinClick(mPosition);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
        }
    }

    private void updateEvent( final String evid, final String email) {
        // Tag used to cancel the request
        String tag_string_req = "req_join_event";
       // pDialog.setMessage("Joining the event ...");
        //showDialog();

        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_UPDATE_EVENT, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Joining Event Response: " + response.toString());
                //hideDialog();

               // Toast.makeText(fragment.getActivity().getApplicationContext(), "Event successfully Joined.", Toast.LENGTH_LONG).show();

                // Launch event fragment
//                Events fragment = new Events();
//                android.support.v4.app.FragmentTransaction fragmentTransaction =
//                        getActivity().getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.fragment_container,fragment);
//                fragmentTransaction.commit();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Joining Event Error: " + error.getMessage());
                //Toast.makeText(getActivity().getApplicationContext(),error.getMessage(), Toast.LENGTH_LONG).show();
                //hideDialog();
            }
        }) {

            @Override
            protected java.util.Map<String, String> getParams() {
                // Posting params to add event url
                java.util.Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                params.put("evid", evid);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
//    private void showDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }
//
//    private void hideDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }

}