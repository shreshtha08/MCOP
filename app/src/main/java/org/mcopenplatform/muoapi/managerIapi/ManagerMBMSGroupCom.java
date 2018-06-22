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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import org.doubango.ngn.NgnEngine;
import org.mcopenplatform.muoapi.BuildConfig;

import java.util.HashMap;
import java.util.Map;


public class ManagerMBMSGroupCom extends ManagerIapiBase 
        implements 
        org.doubango.ngn.services.mbms.IMyMbmsService.MbmsExternalServiceListener ,
        org.doubango.ngn.services.mbms.IMyMbmsService.OnMbmsListener
{
    private final static String TAG = org.mcopenplatform.muoapi.utils.Utils.getTAG(ManagerMBMSGroupCom.class.getCanonicalName());
    private final org.mcopenplatform.iapi.IMBMSGroupComListener.Stub mMBMSGroupComListener;
    public enum StatusTMGI{
        NONE,
        AVAILABLE,
        UNAVAILABLE
    }
    private Map<Long,StatusTMGI> statusTMGIMap;

    protected String PACKET_MAIN_SERVICE="com.expway.embmsserver";
    protected final String PACKET_SERVICE="com.expway.embmsserver.MCOP";
    private org.doubango.ngn.services.mbms.IMyMbmsService mbmsService;

    public ManagerMBMSGroupCom() {
        super();
        mMBMSGroupComListener= new org.mcopenplatform.iapi.IMBMSGroupComListener.Stub() {

            @Override
            public void notifySAIList(int[] SAI) throws RemoteException {
                if(BuildConfig.DEBUG){
                    String stringData="";
                    for(int sai:SAI)stringData=stringData+";"+sai;
                        Log.d(TAG,"notifySAIList: "+stringData);
                }
                //TODO: Should check if any available for this group of TMGI SAIs
            }

            @Override
            public void notifyCellInfo(int MCC, int MNC, int ECI) throws RemoteException {
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"notifyCellInfo: MCC->"+MCC+" MNC->"+MNC+" ECI->"+ECI);
                }
            }

            @Override
            public void notifyMBMSGroupCommAvailability(long TMGI, int available, int goodQuality) throws RemoteException {
                notifyMBMSGroupCommAvailabilityMCOP( TMGI,  available,  goodQuality);
            }

            @Override
            public void notifyOpenMBMSGroupCommResult(long TMGI, int result, String netInterfaceName) throws RemoteException {
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"notifyOpenMBMSGroupCommResult: TMGI->"+TMGI+" result->"+result+" netInterfaceName->"+netInterfaceName);
                }
                if(result>0){
                    if(BuildConfig.DEBUG){
                        Log.d(TAG,"notifyOpenMBMSGroupCommResult: result is ok");
                    }
                    mbmsService.onChangeServiceArea(mbmsService.getSais(TMGI),mContext);
                    mbmsService.startMbmsManager(netInterfaceName,TMGI);
                }
            }

            @Override
            public void notifyCloseMBMSGroupCommResult(long TMGI, int result) throws RemoteException {
                if(BuildConfig.DEBUG){
                    Log.d(TAG,"notifyCloseMBMSGroupCommResult: TMGI->"+TMGI+" result->"+result);
                }
            }
        };
        statusTMGIMap=new HashMap<>();
    }

    private  void notifyMBMSGroupCommAvailabilityMCOP(long TMGI, int available, int goodQuality){
        if(BuildConfig.DEBUG){
            Log.d(TAG,"notifyMBMSGroupCommAvailability: TMGI->"+TMGI+" available->"+available+" goodQuality->"+goodQuality);
        }

        StatusTMGI statusTMGI;
        if(TMGI>0 &&
                (mbmsService.getMbmsDataOfTmgi(TMGI))!=null &&
                (statusTMGI=statusTMGIMap.get(TMGI))!=null){
            if(available>0){
                statusTMGI=(StatusTMGI.AVAILABLE);
            }else{
                statusTMGI=(StatusTMGI.UNAVAILABLE);
            }
            switch (statusTMGI) {
                case AVAILABLE:
                    if(BuildConfig.DEBUG)Log.d(TAG,"notifyMBMSGroupCommAvailability Status: AVAILABLE");
                    try {
                        if(mService!=null){
                            int[] sais=null;
                            sais= mbmsService.getSais(TMGI);
                            int[] frequencies=null;
                            frequencies=mbmsService.getFrequencies(TMGI);
                            if(BuildConfig.DEBUG){
                                String saiString="";
                                if(sais!=null && sais.length>0)
                                for(int sai:sais)saiString=saiString+" "+sai;
                                String freString="";
                                if(frequencies!=null && frequencies.length>0)
                                for(int fre:frequencies)freString=freString+" "+fre;
                                Log.i(TAG,"AVAILABLE TMGI with SAIs: "+sais.length+" frequencies:"+freString );
                            }
                            ((org.mcopenplatform.iapi.IMBMSGroupCom)mService).openGroupComm(TMGI,
                                    sais,frequencies!=null?frequencies:new int[0]);
                        }
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    break;
                case UNAVAILABLE:
                    if(BuildConfig.DEBUG)Log.d(TAG,"notifyMBMSGroupCommAvailability status: UNAVAILABLE");
                case NONE:
                    break;
            }
        }else{
            Log.e(TAG,"Now, we don´t have this TMGI");
        }
    }

    @Override
    protected void startInternal() {
        mbmsService= NgnEngine.getInstance().getMbmsService();
        mbmsService.setMbmsExternalServiceListener(this);
        mbmsService.setOnMbmsListener(this);
    }

    @Override
    protected void stopInternal() {
        //Test
        stopServer(mContext);
    }

    //Test
    protected void stopServer(Context context) {
        Log.d(TAG,"Stopping MBMS SERVER.");
        String mMspPackageName = "com.expway.embmsserver";
        try {
            ComponentName component   = new ComponentName( getPACKET_MAIN_SERVICE(),getPACKET_SERVICE());

            Intent explicitIntent = new Intent();
            explicitIntent.setComponent(component);
            Intent intent = explicitIntent;
            if (intent != null) {
                boolean isStop = context.stopService(intent);

                if (!isStop) {
                    Log.e(TAG, "eMBMS Service not found!");
                } else {
                    Log.v(TAG, "Stopping Service...");
                }
            } else {
                Log.e(TAG, "Could not get explicit intent to eMBMS Service.");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping the eMBMS Service intent.");
            e.printStackTrace();
        }
    }

    @Override
    protected Object registerInterface(IBinder service) {
        org.mcopenplatform.iapi.IMBMSGroupCom imbmsGroupCom = org.mcopenplatform.iapi.IMBMSGroupCom.Stub.asInterface(service);
        Log.d(TAG,"Register notification in "+getPACKET_SERVICE());
        try {
            (imbmsGroupCom).registerApplication(mMBMSGroupComListener);
        } catch (Exception e) {
            Log.e(TAG,e.getLocalizedMessage());
        }
        return imbmsGroupCom;
    }

    @Override
    protected boolean receiveEvent(Message message) {
        if(BuildConfig.DEBUG)
            Log.d(TAG,"Execute receiveEvent in "+getPACKET_SERVICE()+": what: "+message.what);
        return false;
    }

    /**
     * It allows to distinguish between the different PACKET_SERVICE for each of the extended class of ManagerIapiBase
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

    //START EXTERNAL MBMS
    @Override
    public void startedClient(boolean status) {
        //TODO:
    }

    @Override
    public void startedServer(boolean status) {
        //TODO:
    }

    @Override
    public boolean mbmsListeningServiceAreaCurrent(long TMGI, int[] sai, int[] frequencies) {
        //TODO:
        return false;
    }

    @Override
    public void stopServiceMBMS() {
        if(BuildConfig.DEBUG)Log.i(TAG,"Execute stopServiceMBMS");
        //TODO: incorrect
        //Test
        for(long tmgi:statusTMGIMap.keySet()){
            if(mService!=null)
                try {
                    ((org.mcopenplatform.iapi.IMBMSGroupCom)mService).stopMBMSGroupCommMonitoring(tmgi);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public void onNewServiceArea(long TMGI, int[] sai, int[] frequencies, long QCI) {
        if(BuildConfig.DEBUG){
            String sias="";
            for(int sai1:sai)sias=sias+" "+sai1;
            Log.i(TAG,"onNewServiceArea TMGI: "+TMGI+" sias: "+sias);
        }
        if(statusTMGIMap!=null && statusTMGIMap.get(TMGI)==null)
            statusTMGIMap.put(TMGI,StatusTMGI.NONE);
        try {
            if(mService!=null)
            ((org.mcopenplatform.iapi.IMBMSGroupCom)mService).startMBMSGroupCommMonitoring(TMGI,
                    sai,
                    frequencies,
                    (QCI>=Integer.MAX_VALUE || QCI<=Integer.MIN_VALUE)?Integer.MAX_VALUE:(int)QCI);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        //TEST
        notifyMBMSGroupCommAvailabilityMCOP(TMGI, 1,1);
        //TODO: receive new TMGI
    }
}