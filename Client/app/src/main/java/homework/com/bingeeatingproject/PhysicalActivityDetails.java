package homework.com.bingeeatingproject;

import java.io.Serializable;

/**
 * Created by svemulapalli on 11/28/16.
 */

public class PhysicalActivityDetails implements Serializable {

    String duration;
    String date;
    String time;
    String _id;
    String loggedAt;

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    String activity;

    public String getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(String loggedAt) {
        this.loggedAt = loggedAt;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "FoodActivityDetails{" +
                "duration='" + duration + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", _id='" + _id + '\'' +
                ", loggedAt='" + loggedAt + '\'' +
                '}';
    }
}
