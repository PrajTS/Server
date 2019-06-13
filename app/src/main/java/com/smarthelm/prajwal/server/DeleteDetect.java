package com.smarthelm.prajwal.server;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;

import java.sql.Time;

/**
 * Created by Prajwal on 25-10-2017.
 */

public class DeleteDetect implements Runnable {
    Thread delete;
    DatabaseReference d,a;
    public DeleteDetect(){}
    public DeleteDetect(DatabaseReference d,DatabaseReference a)
    {
        this.d=d;
        this.a=a;
        delete=new Thread(this);
        delete.start();;
    }
    public void run()
    {
        try {
            Thread.sleep(1800000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        a.child("accident").setValue(false);
        d.removeValue();
    }


}
