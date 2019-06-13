package com.smarthelm.prajwal.server;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.smarthelm.prajwal.server.MainActivity.mFirebaseDatabase;

public class SmsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle intentExtras = intent.getExtras();

        if (intentExtras != null) {
            /* Get Messages */
            Object[] sms = (Object[]) intentExtras.get("pdus");

            for (int i = 0; i < sms.length; ++i) {
                /* Parse Each Message */
                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) sms[i]);
                String phone = smsMessage.getOriginatingAddress();
                String message = smsMessage.getMessageBody();
                Log.e("Message received: ",message);
                upload(phone,message);
                Toast.makeText(context, phone + ": " + message, Toast.LENGTH_SHORT).show();
                MainActivity.counttxt.setText("Count = "+(++MainActivity.count));
            }
        }
    }

    public void upload(String phone, String message)
    {
        long div = 10000000000L;
        final long userid= Long.parseLong(phone)%div;
        final String data[]=message.split("\\s");
        final DatabaseReference mMessagesDatabaseReference = mFirebaseDatabase.getReference("adetected/"+userid);
        final DatabaseReference a = mFirebaseDatabase.getReference("Users/"+userid);

        if(data.length==10)
        {
            int hr=5,day=0,mon=0,yr=0;
            int min = Integer.parseInt(data[8]);
            min+=30;
            if(min>59)
            {
                hr++;
                min = min-60;
            }
            hr+=Integer.parseInt(data[7]);
            if(hr>23)
            {
                day++;
                hr = hr-24;
            }

            day+=Integer.parseInt(data[4]);
            yr+=Integer.parseInt(data[6]);
            mon+=Integer.parseInt(data[5]);
            if(mon==1 || mon==3|| mon==5 || mon==7 || mon==8 || mon==10 || mon==12)
            {
                if(day>31)
                {
                    mon++;
                    day-=31;
                }
            }
            else if(mon==4|| mon==6 || mon==9 || mon==11)
            {
                if(day>30)
                {
                    mon++;
                    day-=30;
                }
            }
            else
            {
                if(yr%4==0)
                {
                    if(day>29)
                    {
                        mon++;
                        day-=29;
                    }
                }
                else
                {
                    if(day>28)
                    {
                        mon++;
                        day-=28;
                    }
                }
            }

            if(mon>12)
            {
                yr++;
                mon-=12;
                day = 1;
            }

            int len = data[1].length();
            float lat = Integer.parseInt(data[0])+ Float.parseFloat(data[1].substring(0, len-2))/60;
            if(data[1].charAt(len-1) == 'S')
                lat = -1 * lat;
            len = data[3].length();
            float lng = Integer.parseInt(data[2])+ Float.parseFloat(data[3].substring(0, len-2))/60;
            if(data[3].charAt(len-1) == 'W')
                lng = -1 * lng;

            String time  = hr +":"+min+":"+Integer.parseInt(data[9]);
            String[] monthList ={"","January","February","March","April","May","June","July","August","September","October","November","December"};
            String dates = monthList[mon]+" "+day+", "+yr;
            String remarks = "<a target=\"_blank\" href=\"http://www.google.com/maps/search/" + data[0]+"%20"+data[1] + "," + data[2]+"%20"+data[3]+"\">Accident Location</a>";
            final Report rep = new Report(dates,time,"Accident Detected",remarks);

            Values v = new Values(""+lat,""+lng,day,mon,yr,hr,min,Integer.parseInt(data[9]));
            final float finalLat = lat;
            final float finalLng = lng;
            mMessagesDatabaseReference.setValue(v).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    a.child("accident").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            a.child("sendNotification").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mMessagesDatabaseReference.child("report").child("1").setValue(rep);
                                }
                            });
                        }
                    });
                    findHospitals(""+ finalLat, ""+ finalLng, userid);
                }
            });


            //new DeleteDetect(mMessagesDatabaseReference,a);
        }
    }


    String API_KEY = MainActivity.API_KEY;
    String url = "";

    static RequestQueue mQueue;
    public void findHospitals(String lat, String lng, final long userid){

        url="https://maps.googleapis.com/maps/api/distancematrix/json?origins="+lat+","+lng+"&destinations=";
        final DatabaseReference hospitals = FirebaseDatabase.getInstance().getReference("hospitals");

        hospitals.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshot) {
                final ArrayList<String> hospitalId = new ArrayList<String>();

                if(dataSnapshot.hasChildren()){
                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                        hospitalId.add(postSnapshot.getKey());
                        url+=postSnapshot.child("lat").getValue().toString()+"%2C"+postSnapshot.child("lng").getValue().toString()+"%7C";
                    }
                    url = url.substring(0,url.length()-3) + "&key=" + API_KEY;
                    Log.e("hi",url);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try{
                                        JSONArray jsonArray = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
                                        int max =0;

                                        ArrayList<Integer> durationAL = new ArrayList<>();

                                        for(int i=0; i<jsonArray.length(); i++){
                                            String distance = jsonArray.getJSONObject(i).getJSONObject("distance").get("text").toString();
                                            int lenDist = distance.length();
                                            float dist = Float.parseFloat(distance.substring(0,lenDist - 3));

                                            String duration = jsonArray.getJSONObject(i).getJSONObject("duration").get("text").toString();
                                            int lenDura = duration.length();
                                            int dur = Integer.parseInt(duration.substring(0,lenDura-5));
                                            durationAL.add(dur);
                                        }

                                        ArrayList<Integer> sortedDuration = new ArrayList<>(durationAL);
                                        Collections.sort(sortedDuration);

                                        for(int i=0;i<5;i++){
                                            int index = durationAL.indexOf(sortedDuration.get(i));
                                            final String a = hospitalId.get(index);
                                            hospitals.child(a).child("accidents").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    long ind = dataSnapshot.getChildrenCount();
                                                    Boolean flag=true;
                                                    for(DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                                                        Log.e("pre",postSnapshot.getValue().toString()+"---"+userid);
                                                        if(postSnapshot.getValue().toString().equals(""+userid)){
                                                            flag = false;
                                                        }
                                                    }
                                                    if(flag)
                                                        hospitals.child(a).child("accidents").child(ind+"").setValue(userid);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        }
                                    }
                                    catch (Exception e){}
                                }
                            }, new Response.ErrorListener() {

                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // TODO: Handle error

                                }
                            });

                    mQueue.add(jsonObjectRequest);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}