package com.vrseen.vrstore.server;

/**
 * Created by FX on 2016/6/22 14:21.
 * 描述:
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.vrseen.vrhome.UnityCome;
import com.vrseen.vrstore.logic.U3dMediaPlayerLogic;
import com.vrseen.vrstore.util.CommonUtils;

public class UsbdeviceReciver extends BroadcastReceiver {
    private static UsbdeviceReciver    _instance;

    public static UsbdeviceReciver getInstance()
    {

        if(_instance == null)
        {
            _instance = new UsbdeviceReciver();
        }

        return _instance;
    }
    private Context _context = null;
    private boolean usbAttached = false;

    public Context get_context() {
        return _context;
    }

    public void set_context(Context _context) {
        this._context = _context;
        usbAttached();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String actionString = intent.getAction();
        if (UsbManager.ACTION_USB_DEVICE_ATTACHED
                .equals(actionString)) {
            Log.e("attached", "----------------");
            UsbdeviceReciver.getInstance().usbAttached = true;
            UsbdeviceReciver.getInstance().usbAttached();
        } else if (UsbManager.ACTION_USB_DEVICE_DETACHED
                .equals(actionString)) {
            Log.e("deatched", "--------------");

            UsbdeviceReciver.getInstance().usbDetached();
            UsbdeviceReciver.getInstance().usbAttached = false;
        }
    }

    public void usbAttached(){
        if(_context != null && usbAttached) {
            String curActivityName = CommonUtils.getCurActivityName(getAppContext());
            if (!curActivityName.isEmpty() && !curActivityName.contains("GoogleUnityActivity")) {

                U3dMediaPlayerLogic.getInstance().comeinVR(getAppContext(),null);
            }
        }

    }

    public void usbDetached(){
        if(_context != null && usbAttached){
            String curActivityName = CommonUtils.getCurActivityName(getAppContext());
            if (!curActivityName.isEmpty() && curActivityName.contains("GoogleUnityActivity")) {

                UnityCome.exitVR();
            }
        }
    }

    private Context getAppContext(){
        return _context;
    }
}
