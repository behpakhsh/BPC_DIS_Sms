package bpc.dis.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private SmsReceiveListener smsReceiveListener;
    private String smsProviderNumber;

    public SmsBroadcastReceiver() {

    }

    public SmsBroadcastReceiver(String smsProviderNumber, SmsReceiveListener smsReceiveListener) {
        this.smsReceiveListener = smsReceiveListener;
        this.smsProviderNumber = smsProviderNumber;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (smsReceiveListener == null) {
            return;
        }
        if (intent.getExtras() == null) {
            return;
        }
        Object[] objects = (Object[]) intent.getExtras().get("pdus");
        if (objects == null) {
            return;
        }
        StringBuilder message = new StringBuilder();
        for (Object object : objects) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
            String sender = smsMessage.getDisplayOriginatingAddress();
            if (!sender.equals(smsProviderNumber)) {
                return;
            }
            message.append(smsMessage.getMessageBody());
        }
        if (message == null) {
            return;
        }
        smsReceiveListener.messageReceived(message.toString());
    }

}