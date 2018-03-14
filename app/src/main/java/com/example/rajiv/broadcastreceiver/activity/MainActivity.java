package com.example.rajiv.broadcastreceiver.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.rajiv.broadcastreceiver.R;

public class MainActivity extends AppCompatActivity {


    FirstOrderBroadcastReceiver firstOrderBroadcastReceiver;
    SecondOrderBroadcastReceiver secondOrderBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }


    @Override
    protected void onResume() {
        super.onResume();

        //register dynamic broadcast receiver
        //remove receiver from manifest
        IntentFilter intentFilter = new IntentFilter("dynamic.first.receiver");
        firstOrderBroadcastReceiver = new FirstOrderBroadcastReceiver();
        registerReceiver(firstOrderBroadcastReceiver, intentFilter);


        //ACTION_TIME_TICK broadcast can only be received by dynamic broadcast receiver
        intentFilter = new IntentFilter(Intent.ACTION_TIME_TICK);
        secondOrderBroadcastReceiver = new SecondOrderBroadcastReceiver();
        registerReceiver(secondOrderBroadcastReceiver,intentFilter);


    }

    @Override
    protected void onPause() {
        super.onPause();

        //unregister dynamic broadcast receiver
        unregisterReceiver(firstOrderBroadcastReceiver);
        unregisterReceiver(secondOrderBroadcastReceiver);

    }

    public void callFirstBroadcast(View view) {

        //This method sends broadcast to statically typed receiver

        //Use explicit intent if you want a single receiver to trigger and know exactly what its name is.
        //Intent intent = new Intent(this, FirstReceiver.class);

        //Use implicit intent if you one or multiple receiver to trigger and don't know exactly what the receiver's name is.
        //Intent intent = new Intent("rajiv.broadcastreceiver.receiver");


        //Send Dynamic broadcast
        Intent intent = new Intent("dynamic.first.receiver");

        sendBroadcast(intent);
    }

    public void callOrderBroadcast(View view) {

        //to send broadcast to multiple receiver add same intent action property in Manifest.
        //And to set the order in which receiver will response to broadcast add priority property.
        //Note: highest priority will get the first message.

        Intent intent = new Intent("rajiv.broadcastreceiver.activity");

        //use bundle to send data with intents
        Bundle bundle = new Bundle();
        bundle.putString("name", "Rajiv");

        //sendBroadcast(intent);


        //use sendOrderedBroadcast to send broadcast in order based on the priority property defined in Manifest.
        //sendOrderedBroadcast(intent,null);

        //Use this send orderedbroadcast so that you can add int:code , string:data , bundle:extra  in a single method
        sendOrderedBroadcast(intent, null, null, null, 12, "AndroidData", bundle);
    }

    public void timerBroadcast(View view) {

    }


    public static class FirstOrderBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context, "FirstOrderBroadcastReceiver", Toast.LENGTH_SHORT).show();

            //This is to check wather the broadcast is order or not so if its not then app wont crash
            if (isOrderedBroadcast()) {
                Toast.makeText(context, "Name: " + getResultExtras(true).getString("name", ""), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static class SecondOrderBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Toast.makeText(context, "SecondOrderBroadcastReceiver", Toast.LENGTH_SHORT).show();

            if (isOrderedBroadcast()) {

                Bundle bundle = getResultExtras(true);

                Toast.makeText(context, "IntCode: " + getResultCode() + " | StringData: " + getResultData() + " | BundleExtra: " + bundle.getString("name"), Toast.LENGTH_SHORT).show();

                bundle.putString("name", "New Name");

                //use this method to change/ manipulate the result code, data and extra for next receiver
                setResult(14, "AndroidData2", bundle);

                //use this to cancel the orderedbroadcast so the next receiver cannot get broadcast to respond to
                abortBroadcast();
            }

        }
    }
}
