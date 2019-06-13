package com.smarthelm.prajwal.server;

/**
 * Created by Prajwal on 24-10-2017.
 */

public class Values {
    int day,month,year,hour,min,sec;
    String lat,lon, reportStatus;
    boolean falseAlarm;
    public Values(){}

    public Values (String lat,String lon,int day,int month,int year,int hour,int min,int sec)
    {
        this.day=day;
        this.month=month;
        this.year=year;
        this.hour=hour;
        this.min=min;
        this.sec=sec;
        this.lat=lat;
        this.lon=lon;
        this.falseAlarm = false;
        this.reportStatus = "Accident Detected";
    }

    public void setDay(int day)
    {
        this.day=day;
    }

    public void setMonth(int month)
    {
        this.month=month;
    }
    public void setYear(int year)
    {
        this.year = year;
    }

    public void setHour(int hour)
    {
        this.hour = hour;
    }

    public void setMin (int min)
    {
        this.min = min;
    }

    public void setSec (int sec)
    {
        this.sec = sec;
    }

    public void setLat(String lat)
    {
        this.lat = lat;
    }

    public void setLon(String lon)
    {
        this.lon = lon;
    }

    public void setFalseAlarm(boolean falseAlarm) {
        this.falseAlarm = falseAlarm;
    }

    public boolean getFalseAlarm()
    {
        return falseAlarm;
    }
    public int getDay()
    {
        return day;
    }
    public int getMonth(){return month;}

    public int getYear()
    {
        return year;
    }

    public int getHour()
    {
        return hour;
    }
    public int getMin(){
        return min;
    }

    public int getSec(){
        return sec;
    }

    public String getLat(){return lat;}

    public String getLon(){return lon;}

    public void setReportStatus(String responseStatus) {
        this.reportStatus = responseStatus;
    }

    public String getReportStatus() {
        return reportStatus;
    }
}



