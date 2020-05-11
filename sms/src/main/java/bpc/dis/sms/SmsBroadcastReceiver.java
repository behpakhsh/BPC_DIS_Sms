package bpc.dis.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

import java.util.ArrayList;
import java.util.List;

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private List<String> smsProviderNumbers;
    private SmsReceiveListener smsReceiveListener;

    public SmsBroadcastReceiver() {

    }

    public SmsBroadcastReceiver(String smsProviderNumber, SmsReceiveListener smsReceiveListener) {
        this.smsReceiveListener = smsReceiveListener;
        this.smsProviderNumbers = new ArrayList<>();
        this.smsProviderNumbers.add(smsProviderNumber);
    }

    public SmsBroadcastReceiver(List<String> smsProviderNumbers, SmsReceiveListener smsReceiveListener) {
        this.smsReceiveListener = smsReceiveListener;
        this.smsProviderNumbers = smsProviderNumbers;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (smsReceiveListener != null) {
            if (intent.getExtras() != null) {
                Object[] objects = (Object[]) intent.getExtras().get("pdus");
                if (objects != null) {
                    boolean isFromRequestedProviderNumber = false;
                    StringBuilder message = new StringBuilder();
                    for (Object object : objects) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
                        message.append(smsMessage.getMessageBody());
                        String sender = smsMessage.getDisplayOriginatingAddress();
                        for (String smsProviderNumber : smsProviderNumbers) {
                            if (smsProviderNumber.equals(sender)) {
                                isFromRequestedProviderNumber = true;
                                break;
                            }
                        }
                    }
                    if (isFromRequestedProviderNumber) {
                        if (!String.valueOf(message).isEmpty()) {
                            smsReceiveListener.messageReceived(message.toString());
                        }
                    }
                }
            }
        }
    }

}