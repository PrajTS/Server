package com.smarthelm.prajwal.server;

public class Report {
    String date, time, value,remarks;
    public Report(){}

    public Report(String date, String time, String value, String remarks){
        this.date = date;
        this.time = time;
        this.remarks = remarks;
        this.value = value;
    }

    public String getDate() {
        return date;
    }

    public String getRemarks() {
        return remarks;
    }

    public String getTime() {
        return time;
    }

    public String getValue() {
        return value;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setValue(String value) {
        this.value = value;
    }
}

