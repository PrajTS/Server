package com.smarthelm.prajwal.server;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    public static FirebaseDatabase mFirebaseDatabase;
    public static DatabaseReference mMessagesDatabaseReference;

    boolean acc=false;

    String msg="";
    String mmsg="";
    String number,name;
    static SmsManager smsManager;

    private static final String TAG = "MainActivity";
    static int count=0;
    static TextView counttxt;
    private static final int SMS_PERMISSION_CODE = 0;

    public static String API_KEY ;

    boolean isActive = false;
    boolean isNotify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        API_KEY = getString(R.string.api_key);

        SmsReceiver.mQueue = Volley.newRequestQueue(this);
        if (!hasReadSmsPermission()) {
            requestReadAndSendSmsPermission();
        }
        else
            Log.e("sms","granted");

        smsManager = SmsManager.getDefault();

        counttxt =(TextView)findViewById(R.id.count);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mMessagesDatabaseReference = mFirebaseDatabase.getReference("adetected");

        mMessagesDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
//                Log.e("a","Child added");
//                Log.e("key", "" + dataSnapshot.getKey());
//                number = dataSnapshot.getKey();
//
//                if(dataSnapshot.hasChild("sendNotification") && (Boolean)dataSnapshot.child("sendNotification").getValue()){
//                    Log.e("h",dataSnapshot.child("sendNotification").getValue().toString());
//                Values values = dataSnapshot.getValue(Values.class);
//                msg = " has met with an accident at " + values.hour + ":" + values.min + ":" + values.sec + " on " + values.day + "/" + values.month + "/" + values.year + ".\nUpdates: \n https://smart-helm-web.herokuapp.com/emc/" + number;
//
//                DatabaseReference users = mFirebaseDatabase.getReference().child("Users").child("" + number);
//
//                users.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        if ((Boolean) dataSnapshot.child("accident").getValue()) {
//                            Log.e("in",dataSnapshot.child("accident").getValue().toString());
//                            acc = true;
//                            name=dataSnapshot.child("Name").getValue().toString();
//                            Log.e("acc", "" + acc);
//                            long c = dataSnapshot.child("emC").getChildrenCount();
//                            Log.e("count",""+c);
//                            mmsg = name.toString()+ " "+msg.toString();
//                            for(int i=1;i<=4;i++)
//                            {
//                                try {
//                                    long emC = (long) dataSnapshot.child("emC").child("phone" + i).getValue();
//                                    Log.e("dsa", mmsg);
//                                    smsManager.sendTextMessage("" + emC, null, mmsg, null, null);
//                                }
//                                catch (Exception e){Log.e("gh",e.toString());}
//                            }
//                            acc=false;
//                            name="";
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });}
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                isActive = false;
                isNotify = false;

                final DatabaseReference userInfo = mFirebaseDatabase.getReference().child("Users").child("" + dataSnapshot.getKey());
                userInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((Boolean) dataSnapshot.child("accident").getValue())
                            isActive = true;
                        if((Boolean) dataSnapshot.child("sendNotification").getValue())
                            isNotify = true;
                        handle(dataSnapshot.getKey());
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        isActive = false;
                        isNotify = false;
                    }
                });
                Log.e("info","key : "+dataSnapshot.getKey()+" | ac : "+isActive+" | noti : "+isNotify);
                Log.e("a","Child changed");


            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

//                Log.e("a","Child changed");
//                try {
//                    Log.e("key", "" + dataSnapshot.getKey());
//                    msg = " is fine. It was a false alarm.";
//                }
//                catch (Exception e){}
//                DatabaseReference users = mFirebaseDatabase.getReference().child("Users").child("" + dataSnapshot.getKey());
//
//                users.addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                            name=dataSnapshot.child("Name").getValue().toString();
//                            long c = dataSnapshot.child("emC").getChildrenCount();
//                            Log.e("count",""+c);
//                            for(int i=1;i<=c;i++)
//                            {
//                                try {
//                                    long emC = (long) dataSnapshot.child("emC").child("phone" + i).getValue();
//                                    sendMessage("" + emC, name + " " + msg);
//                                }
//                                catch (Exception e){}
//                            }
//                            name="";
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//                    }
//                });

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    private void handle(final String key){

        if(isNotify){
            final DatabaseReference users = mFirebaseDatabase.getReference().child("Users").child("" + key);
            users.child("sendNotification").setValue(false).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Log.e("key", "" + key);
                    //number = dataSnapshot.getKey();

                    DatabaseReference accInfo = mFirebaseDatabase.getReference().child("adetected").child(""+key);
                    accInfo.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Values values = dataSnapshot.getValue(Values.class);
                            if(values.falseAlarm == true){
                                users.child("accident").setValue(false);
                                users.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if (!(Boolean) dataSnapshot.child("accident").getValue()) {
                                            Log.e("msg = ", name+" "+msg);
                                            name=dataSnapshot.child("Name").getValue().toString();
                                            Log.e("acc", "" + acc);
                                            msg = " is fine. It was a false alarm.";
                                            for(int i=1;i<=4;i++)
                                            {
                                                try {
                                                    long emC = (long) dataSnapshot.child("emC").child("phone" + i).getValue();
                                                    sendMessage(""+emC, name+" "+msg);

                                                    Log.e("msgSend",name + " " + msg);
                                                }
                                                catch (Exception e){}
                                            }
                                            name="";
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }

                            else if(!values.falseAlarm && isActive){
                                DatabaseReference users = mFirebaseDatabase.getReference().child("Users").child("" + key);
                                msg = " has met with an accident at " + values.hour + ":" + values.min + ":" + values.sec + " on " + values.day + "/" + values.month + "/" + values.year + ".\nUpdate: \n https://smart-helm-web.herokuapp.com/emc/"+key;
                                Log.e("inin",msg);
                                users.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        if ((Boolean) dataSnapshot.child("accident").getValue()) {
                                            Log.e("msg = ", name+" "+msg);
                                            name=dataSnapshot.child("Name").getValue().toString();
                                            Log.e("acc", "" + acc);
                                            for(int i=1;i<=4;i++)
                                            {
                                                try {
                                                    long emC = (long) dataSnapshot.child("emC").child("phone" + i).getValue();
                                                    sendMessage(""+emC, name+" "+msg);
                                                    Log.e("msgSend",name + " " + msg);
                                                }
                                                catch (Exception e){}
                                            }
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }
            });

        }
    }


    /**
     * Runtime permission
     */
    private boolean hasReadSmsPermission() {
        return (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) && (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestReadAndSendSmsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_SMS) && ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)) {
            Log.d(TAG, "shouldShowRequestPermissionRationale(), no permission requested");
            return;
        }
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.SEND_SMS},1);
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_SMS},
                SMS_PERMISSION_CODE);

    }

    static void sendMessage (String number,String msg)
    {
        try {
            //SmsManager smsManager = SmsManager.getDefault();
                //SmsManager.getSmsManagerForSubscriptionId(1).sendTextMessage("8317399805",null, msg,null,null);
                MainActivity.smsManager.sendTextMessage(number, null, msg.trim(), null, null);
            Log.e("msg",msg.trim());
            Log.e("number",number);
            //Toast.makeText(getApplicationContext(), "SMS Sent!",
                    //Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            //Toast.makeText(getApplicationContext(),
                    //"SMS failed, please try again later!",
                   // Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}

