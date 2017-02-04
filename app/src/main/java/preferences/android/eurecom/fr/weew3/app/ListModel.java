package preferences.android.eurecom.fr.weew3.app;

/**
 * Created by aabdelli on 03/02/2017.
 */
public class ListModel {

    private String email= "";
    private String event_type= "";
    private String event_date= "";
    private String time_begin= "";
    private String time_end= "";
    private String description= "";
    private String loc_long= "";
    private String loc_lat= "";
    private String picture= "";

    public String getEvent_type() {
        return event_type;
    }

    public String getEvent_date() {
        return event_date;
    }

    public String getTime_begin() {
        return time_begin;
    }

    public String getTime_end() {
        return time_end;
    }

    public String getDescription() {
        return description;
    }

    public String getLoc_long() {
        return loc_long;
    }

    public String getLoc_lat() {
        return loc_lat;
    }

    public String getPicture() {
        return picture;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEvent_type(String event_type) {
        this.event_type = event_type;
    }

    public void setEvent_date(String event_date) {
        this.event_date = event_date;
    }

    public void setTime_begin(String time_begin) {
        this.time_begin = time_begin;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLoc_long(String loc_long) {
        this.loc_long = loc_long;
    }

    public void setLoc_lat(String loc_lat) {
        this.loc_lat = loc_lat;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getEmail() {
        return email;
    }
}
