package preferences.android.eurecom.fr.weew3;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;


/**
 * A simple {@link Fragment} subclass.
 */
public class Events extends Fragment  {


    public Events() {
        // Required empty public constructor

    }
    //Spinner spinner = (Spinner) view.findViewById(R.id.eventTypeSpinner);
    // Create an ArrayAdapter using the string array and a default spinner layout

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_events, container, false);
    }

}
