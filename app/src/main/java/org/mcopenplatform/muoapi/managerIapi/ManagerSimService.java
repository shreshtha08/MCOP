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

package org.mcopenplatform.muoapi.managerIapi;

import android.content.Context;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import org.mcopenplatform.iapi.Constants;
import org.mcopenplatform.iapi.ISimService;
import org.mcopenplatform.muoapi.BuildConfig;
import org.mcopenplatform.muoapi.R;


public class ManagerSimService extends ManagerIapiBase{
    private final static String TAG = org.mcopenplatform.muoapi.utils.Utils.getTAG(ManagerSimService.class.getCanonicalName());

    //TODO: slotSIM must be defined by capabilities
    protected static int slotSIMCurrent= Constants.Sim.SimSlot.SLOT_ID_2;
    //TODO: SimApp must be defined by capabilities
    //IMP: Tested cards use USIM
    protected static int simAppCurrent= Constants.Sim.SimApp.USIM;
    //TODO: SimAuth must be defined by capabilities
    protected static int simAuthCurrent= Constants.Sim.SimAuth.AKA;

    public static final int ERROR_CONFIGURATION=104;

    protected final String PACKET_SERVICE="org.mcopenplatform.iapi.SimService";
    private OnSimServiceListener onSimServiceListener;

    public ManagerSimService() {
        super();
    }

    @Override
    protected void startInternal() {

    }

    @Override
    protected void stopInternal() {

    }

    @Override
    protected Object registerInterface(IBinder service) {
        ISimService serviceInterface = ISimService.Stub.asInterface(service);
        Log.d(TAG,"Register notification in "+getPACKET_SERVICE());
        try {
            (serviceInterface).registerNotificationReceiver(mcopMessenger);
        } catch (Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
        }
        return serviceInterface;
    }

    @Override
    protected boolean receiveEvent(Message message) {
        if(BuildConfig.DEBUG)
            Log.d(TAG,"Execute receiveEvent in "+getPACKET_SERVICE()+": what: "+message.what);
        return false;
    }

    /**
     * It allows to distinguish between the different PACKET_SERVICEs for each of the extended class of ManagerIapiBase
     * @return PACKET_SERVICE constant
     */
    @Override
    protected String getPACKET_SERVICE() {
        return PACKET_SERVICE;
    }

    @Override
    protected String getPACKET_MAIN_SERVICE() {
        return PACKET_MAIN_SERVICE;
    }

    protected boolean startConfigurate(Context context){
        if(mService!=null){
            try {
                //TODO: Not sure if pcscf and impu should be used at all the times
                String[] pcscfSlot=getPcscf();
                String[] impu=getImpu();
                String impi=getImpi();
                String domain=getDomain();
                String imei=getImei();
                String imsi=getImsi();
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"Configuration client:");
                    if(pcscfSlot!=null && pcscfSlot.length>0) Log.d(TAG,"pcscf: "+pcscfSlot[0]);
                    if(impu!=null && impu.length>0) Log.d(TAG,"impu: "+impu[0]);
                    if(impi!=null) Log.d(TAG,"impi: "+impi);
                    if(domain!=null) Log.d(TAG,"domain: "+domain);
                    if(imei!=null) Log.d(TAG,"imei: "+imei);
                    if(imsi!=null) Log.d(TAG,"imsi: "+imsi);
                }
                //TODO: Process all data and make the business logic
                if(onSimServiceListener!=null)onSimServiceListener.onConfiguration(impu[0],impi,domain,pcscfSlot[0],4,imsi,imei);
            } catch (RemoteException e) {
                if(onIapiListener!=null)onIapiListener.onIapiError(ERROR_CONFIGURATION,mContext.getString(R.string.Not_be_connected_with_the_service)+getPACKET_SERVICE());
            }
        }else{
            if(onIapiListener!=null)onIapiListener.onIapiError(ERROR_CONFIGURATION,mContext.getString(R.string.Error_when_accessing_service)+getPACKET_SERVICE());
            return false;
        }
        return true;
    }

    //START METHOD
    private String[] getPcscf() throws RemoteException {
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getPcscf Error");
            return null;
        }
        return ((ISimService)mService).getPcscf(slotSIMCurrent);
    }

    private String[] getImpu() throws RemoteException {
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getImpu Error");
            return null;
        }
        return ((ISimService)mService).getImpu(slotSIMCurrent);
    }

    private String getImsi() throws RemoteException {
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getImsi Error");
            return null;
        }
        return ((ISimService)mService).getSubscriberIdentity(slotSIMCurrent);
    }

    private String getImei() throws RemoteException {
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getImei Error");
            return null;
        }
        return ((ISimService)mService).getDeviceIdentity(slotSIMCurrent);
    }

    private String getImpi() throws RemoteException {
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getImpi Error");
            return null;
        }
        return ((ISimService)mService).getImpi(slotSIMCurrent);
    }

    private String getDomain() throws RemoteException {
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getDomain Error");
            return null;
        }
        return ((ISimService)mService).getDomain(slotSIMCurrent);
    }

    public String getAuthentication(String data) throws RemoteException {
        if(mService==null){
            if(BuildConfig.DEBUG)Log.e(TAG,"getAuthentication Error");
            return null;
        }else{
            if(BuildConfig.DEBUG)Log.i(TAG,"Executing getAuthentication: "+data);
        }
        return ((ISimService)mService).getAuthentication(slotSIMCurrent,simAppCurrent,simAuthCurrent,data);
    }
    //END METHOD

    interface OnSimServiceListener{
        void onConfiguration(
                String impu,
                String impi,
                String domain,
                String pcscf,
                int pcscfPort,
                String imsi,
                String imei
        );
    }

    public void setOnSimServiceListener(OnSimServiceListener onSimServiceListener){
        this.onSimServiceListener=onSimServiceListener;
    }
}