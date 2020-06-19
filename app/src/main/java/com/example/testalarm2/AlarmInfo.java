package com.example.testalarm2;

import android.database.sqlite.SQLiteOpenHelper;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AlarmInfo implements Serializable {
    private String hour;
    private String  minute;
    private String drugText;
    private String id;
    private String ampm;


    //id값을 불러올때 사용되는 생성자
    public AlarmInfo(String hour, String  minute, String drugText, String ampm ,String id){
        this.hour = hour;
        this.minute = minute;
        this.drugText = drugText;
        this.ampm = ampm;
        this.id=id;
    }

    //게시물 등록할때 id값이 필요없기때문에 id값이 없는 생성자가 필요
    public AlarmInfo(String  hour, String  minute, String drugText, String ampm ){
        this.hour = hour;
        this.minute = minute;
        this.drugText = drugText;
        this.ampm = ampm;
    }
    public Map<String,Object> getAlarmInfo(){
        Map<String, Object> mData = new HashMap<>();
        mData.put("hour",hour);
        mData.put("minute",minute);
        mData.put("drugtext",drugText);
        mData.put("ampm",ampm);
        return mData;
    }

    public String  getHour() {
        return hour;
    }

    public void setHour(String  hour) {
        this.hour = hour;
    }

    public String  getMinute() {
        return minute;
    }

    public void setMinute(String  minute) {
        this.minute = minute;
    }

    public String getDrugText() {
        return drugText;
    }

    public void setDrugText(String drugText) {
        this.drugText = drugText;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAmpm() {
        return ampm;
    }

    public void setAmpm(String ampm) {
        this.ampm = ampm;
    }

}
