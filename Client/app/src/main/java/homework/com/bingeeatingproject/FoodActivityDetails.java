package homework.com.bingeeatingproject;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by svemulapalli on 11/28/16.
 */

public class FoodActivityDetails implements Serializable{
String notes,drinks,food,place,time,_id,loggedAt,date;
    boolean lexative,vomiting,binge;
    transient Bitmap image;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getDrinks() {
        return drinks;
    }

    public void setDrinks(String drinks) {
        this.drinks = drinks;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(String loggedAt) {
        this.loggedAt = loggedAt;
    }

    public boolean isLexative() {
        return lexative;
    }

    public void setLexative(boolean lexative) {
        this.lexative = lexative;
    }

    public boolean isVomiting() {
        return vomiting;
    }

    public void setVomiting(boolean vomiting) {
        this.vomiting = vomiting;
    }

    public boolean isBinge() {
        return binge;
    }

    public void setBinge(boolean binge) {
        this.binge = binge;
    }

    @Override
    public String toString() {
        return "FoodActivityDetails{" +
                "notes='" + notes + '\'' +
                ", image='" + image + '\'' +
                ", drinks='" + drinks + '\'' +
                ", food='" + food + '\'' +
                ", place='" + place + '\'' +
                ", time='" + time + '\'' +
                ", _id='" + _id + '\'' +
                ", loggedAt='" + loggedAt + '\'' +
                ", lexative=" + lexative +
                ", vomiting=" + vomiting +
                ", binge=" + binge +
                '}';
    }
}
