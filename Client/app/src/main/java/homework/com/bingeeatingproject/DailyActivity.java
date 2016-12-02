package homework.com.bingeeatingproject;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by svemulapalli on 11/28/16.
 */

public class DailyActivity  {

    String activityName;
    String dateTime;
    Bitmap bitmap;
    FoodActivityDetails foodActivity;
    PhysicalActivityDetails physicalActivity;

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public FoodActivityDetails getFoodActivity() {
        return foodActivity;
    }

    public void setFoodActivity(FoodActivityDetails foodActivity) {
        this.foodActivity = foodActivity;
    }

    public PhysicalActivityDetails getPhysicalActivity() {
        return physicalActivity;
    }

    public void setPhysicalActivity(PhysicalActivityDetails physicalActivity) {
        this.physicalActivity = physicalActivity;
    }
}
