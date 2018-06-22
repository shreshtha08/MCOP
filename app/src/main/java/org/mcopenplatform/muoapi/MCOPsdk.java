/*
 *
 *  Copyright (C) 2018 Eduardo Zarate Lasurtegui
 *   Copyright (C) 2018, University of the Basque Country (UPV/EHU)
 *
 *  Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
 *
 *  This file is part of MCOP MCPTT Client
 *
 *  This is free software: you can redistribute it and/or modify it under the terms of
 *  the GNU General Public License as published by the Free Software Foundation, either version 3
 *  of the License, or (at your option) any later version.
 *
 *  This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License along
 *  with this program; if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package org.mcopenplatform.muoapi;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import org.mcopenplatform.muoapi.utils.Utils;


/**
 * MCOP SDK Service
 * @author Eduardo Zarate Lasurtegui
 * @version 0.1
 */
public class MCOPsdk extends Service {
    private final static String TAG = Utils.getTAG(MCOPsdk.class.getCanonicalName());

    private Engine engine;

    public MCOPsdk() {
        engine=Engine.getInstance();
    }

    /**
     * Link the Service with a client. MCOP Service is started at this point.
     * @param intent data received from the client.
     * @return returns the Binder to be used by the binded client to comunicate with the server using the defined AIDL.
     */
    @Override
    public IBinder onBind(Intent intent) {
        if(BuildConfig.DEBUG)Log.d(TAG,"onBind");
        return engine.newClient(intent,this);
    }

    /**
     * Link the Service with a client. MCOP Service is started at this point.
     * @param intent data received from the client.
     * @return returns the Binder to be used by the binded client to comunicate with the server using the defined AIDL
     */
    @Override
    public void onRebind(Intent intent) {
        // TODO: Return the communication channel to the service.
        if(BuildConfig.DEBUG)Log.d(TAG,"onRebind");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        boolean result;
        result=super.onUnbind(intent);
        if(BuildConfig.DEBUG)Log.d(TAG,"onUnbind");
        if(engine!=null) engine.stopClients();
        //When returning false, the new client that calls bindService() will execute OnBind. When true, it will execute onRebind().
        /**
         * It is very important that the answer is always true to run the onRebind() when a new client is connected.
         */
        return false;
    }

    @Override
    public void onDestroy() {
        if(BuildConfig.DEBUG) Log.d(TAG,"onDestroy");
        engine.onDestroyClients();
    }
}
