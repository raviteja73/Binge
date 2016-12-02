package homework.com.bingeeatingproject;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by svemulapalli on 11/30/16.
 */

public class WeeklyActivity implements Serializable{

    String weekNo;
    String goodDays;
    String fgoalReachedDays;
    String gPhysicaldays;
    String events;
    String startDate;

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    String endDate;
    int totalNoOfBinges,totalNoOfVo,totalWeight;

    public int getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(int totalWeight) {
        this.totalWeight = totalWeight;
    }

    public int getTotalNoOfVo() {
        return totalNoOfVo;
    }

    public void setTotalNoOfVo(int totalNoOfVo) {
        this.totalNoOfVo = totalNoOfVo;
    }

    public int getTotalNoOfBinges() {
        return totalNoOfBinges;
    }

    public void setTotalNoOfBinges(int totalNoOfBinges) {
        this.totalNoOfBinges = totalNoOfBinges;
    }

    public String getEvents() {
        return events;
    }

    public void setEvents(String events) {
        this.events = events;
    }

    public String getgPhysicaldays() {
        return gPhysicaldays;
    }

    public void setgPhysicaldays(String gPhysicaldays) {
        this.gPhysicaldays = gPhysicaldays;
    }

    public String getFgoalReachedDays() {
        return fgoalReachedDays;
    }

    public void setFgoalReachedDays(String fgoalReachedDays) {
        this.fgoalReachedDays = fgoalReachedDays;
    }

    public String getGoodDays() {
        return goodDays;
    }

    public void setGoodDays(String goodDays) {
        this.goodDays = goodDays;
    }

    public String getWeekNo() {
        return weekNo;
    }

    public void setWeekNo(String weekNo) {
        this.weekNo = weekNo;
    }
}
