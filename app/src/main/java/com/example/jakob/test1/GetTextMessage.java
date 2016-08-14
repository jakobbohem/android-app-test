package com.example.jakob.test1;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

/**
 * Created by jakob on 14/08/16.
 */
public class GetTextMessage extends BroadcastReceiver {
    private static final String LOG_TAG = "SMSApp";
    /* package */

    static final String ACTION = "android.provider.Telephony.SMS_RECEIVED";
    public void onReceive(Context context, Intent intent){
        if (intent.getAction().equals(ACTION)){
            Bundle bundle = intent.getExtras();
            if (bundle != null){
                Object[] pdus = (Object[]) bundle.get("pdus");
                SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++){
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                for (SmsMessage message : messages){
                    String strFrom = message.getDisplayOriginatingAddress();
                    String strMsg = message.getDisplayMessageBody();

                    if(strMsg.startsWith("SHOW"))
                    {
                        System.out.println("GOT MESSAGE::: "+strMsg);
                    }
                }
            }
        }
    }
}
