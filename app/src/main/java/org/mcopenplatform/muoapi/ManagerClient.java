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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.RemoteException;
import android.util.Log;

import org.doubango.ngn.BuildConfig;
import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.affiliation.affiliationcommand.CommandList;
import org.doubango.ngn.datatype.affiliation.pidf.AffiliationType;
import org.doubango.ngn.datatype.affiliation.pidf.Presence;
import org.doubango.ngn.datatype.affiliation.pidf.StatusType;
import org.doubango.ngn.datatype.affiliation.pidf.Tuple;
import org.doubango.ngn.datatype.gms.pocListService.ns.list_service.ListServiceType;
import org.doubango.ngn.events.NgnEventArgs;
import org.doubango.ngn.events.NgnRegistrationEventArgs;
import org.doubango.ngn.media.NgnMediaType;
import org.doubango.ngn.services.INgnSipService;
import org.doubango.ngn.services.affiliation.IMyAffiliationService;
import org.doubango.ngn.sip.NgnAVSession;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.ngn.utils.NgnUriUtils;
import org.mcopenplatform.muoapi.datatype.error.Constants;
import org.mcopenplatform.muoapi.datatype.group.GroupAffiliation;
import org.mcopenplatform.muoapi.datatype.group.GroupInfo;
import org.mcopenplatform.muoapi.managerIapi.EngineIapi;
import org.mcopenplatform.muoapi.session.ManagerSessions;
import org.mcopenplatform.muoapi.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.mcopenplatform.muoapi.utils.Utils.checkGroupIsExist;
import static org.mcopenplatform.muoapi.utils.Utils.isAffiliatedGroup;
import static org.mcopenplatform.muoapi.utils.Utils.isAffiliatingGroup;
import static org.mcopenplatform.muoapi.utils.Utils.isDeaffiliatedGroup;
import static org.mcopenplatform.muoapi.utils.Utils.isDeaffiliatingGroup;
import static org.mcopenplatform.muoapi.utils.Utils.validationCallType;

public class ManagerClient implements
        IMyAffiliationService.OnAffiliationServiceListener
        , ManagerSessions.OnManagerSessionListener
        , INgnSipService.OnAuthenticationListener
{




    private final static String TAG = org.mcopenplatform.muoapi.utils.Utils.getTAG(ManagerClient.class.getCanonicalName());
    private ArrayList<Long> sessionsID;

    private IBinder iBinder;
    private IMCOPCallback mMCOPCallback;
    private NgnEngine ngnEngine;
    private INgnSipService ngnSipService;
    private IMyAffiliationService myAffiliationService;
    private ManagerSessions managerSessions;
    private Context context;
    private ManagerClient thisInstance;
    private BroadcastReceiver mSipBroadcastRecvRegister;
    private ServiceConnection mConnectionSimService;
    private EngineIapi engineIapi;

    public ManagerClient(Context context) {
        this.context=context;
        engineIapi=EngineIapi.getInstance();
        ngnEngine=NgnEngine.getInstance();
        thisInstance=this;



    }


    public IBinder startManagerClient(){
        if(BuildConfig.DEBUG)Log.d(TAG,"startManagerClient");
        //Start NgnEngine
        if(!ngnEngine.isStarted()){
            Log.d(TAG,"Initialize MCOP Service");
            if(ngnEngine.start()){
                Log.d(TAG,"MCOP Service: Started");
                ngnSipService=ngnEngine.getSipService();
                ngnSipService.setOnAuthenticationListener(this);

                myAffiliationService=ngnEngine.getAffiliationService();
                //Start Manager session
                managerSessions=ManagerSessions.getInstance(context);
                managerSessions.setOnManagerSessionListener(this);
                sessionsID=new ArrayList<>();
                //Affiliation
                ngnEngine.getAffiliationService().setOnAffiliationServiceListener(thisInstance);
            }else{
                Log.e(TAG,"MCOP Service: Error");
            }
            if(mSipBroadcastRecvRegister==null){
                mSipBroadcastRecvRegister = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        final String action = intent.getAction();

                        // Registration Event
                        if(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT.equals(action)){
                            NgnRegistrationEventArgs args = intent.getParcelableExtra(NgnEventArgs.EXTRA_EMBEDDED);
                            if(args == null){
                                Log.e(TAG, "Invalid event arguments");
                                return;
                            }
                            switch(args.getEventType()){
                                case REGISTRATION_NOK:
                                    if(BuildConfig.DEBUG)Log.d(TAG,"REGISTRATION_NOK");
                                    sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVII);
                                    sendLoginEvent(false);
                                    break;
                                case UNREGISTRATION_OK:
                                    if(BuildConfig.DEBUG)Log.d(TAG,"UNREGISTRATION_OK");
                                    sendUnLoginEvent(true);
                                    break;
                                case REGISTRATION_OK:
                                    if(BuildConfig.DEBUG)Log.d(TAG,"REGISTRATION_OK");
                                    //Send affiliation from groups implicit
                                    affiliationImplicitGroups();
                                    sendLoginEvent(true);
                                    break;
                                case REGISTRATION_INPROGRESS:
                                    if(BuildConfig.DEBUG)Log.d(TAG,"REGISTRATION_INPROGRESS");
                                    //TODO:Logical for register
                                    break;
                                case UNREGISTRATION_INPROGRESS:
                                    if(BuildConfig.DEBUG)Log.d(TAG,"UNREGISTRATION_INPROGRESS");
                                    //TODO:Logical for register
                                    break;
                                case UNREGISTRATION_NOK:
                                    if(BuildConfig.DEBUG)Log.d(TAG,"UNREGISTRATION_NOK");
                                    sendErrorUnLoginEvent(Constants.ConstantsErrorMCOP.UnLoginEventError.CCVII);
                                    sendUnLoginEvent(false);
                                    break;
                            }
                        }
                    }
                };
                final IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(NgnRegistrationEventArgs.ACTION_REGISTRATION_EVENT);
                context.registerReceiver(mSipBroadcastRecvRegister, intentFilter);
            }
        }else{
            if(BuildConfig.DEBUG)Log.e(TAG,"Engine started");
        }
        //Start and Binder to services
        if(!engineIapi.isStarted()){
           engineIapi.start(context);
        }else{
            if(BuildConfig.DEBUG)Log.e(TAG,"EngineIapi started");
        }

        iBinder=new IMCOPsdk.Stub() {
            @Override
            public String getMCOPCapabilities() throws RemoteException {
                return null;
            }


            @Override
            public boolean loginMCOP() throws RemoteException {
                boolean result=false;
                if(ngnEngine.isStarted() && ngnSipService.isRegistered()){
                    sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCV);
                    result=false;
                }else{

                }
                return result;
            }

            @Override
            public boolean unLoginMCOP() throws RemoteException {
                Log.d(TAG,"Initialize unLogin Process");
                if(ngnSipService.isRegistered()){
                    ngnSipService.unRegister();
                    return true;
                }else{
                    Log.e(TAG,"Device unregistered");
                    sendErrorUnLoginEvent(Constants.ConstantsErrorMCOP.UnLoginEventError.CCV);
                }
                return false;
            }

            @Override
            public boolean authorizeUser(String url) throws RemoteException {
                boolean result=false;
                Uri uri=null;
                try {
                    if(url==null || url.trim().isEmpty() || (uri=Uri.parse(url))==null){
                        result=false;
                        // For testing purposes only
                        // To avoid using IDMS/CMS
                        registerNow();
                        if(url!=null){
                            if(BuildConfig.DEBUG)Log.e(TAG,Constants.ConstantsErrorMCOP.LoginEventError.CCVI.getString());
                            sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVI);
                        }
                    }else{
                    }
                }catch (Exception e){
                    if(BuildConfig.DEBUG)Log.e(TAG,Constants.ConstantsErrorMCOP.LoginEventError.CCVI.getString());
                    sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVI);
                }
                return result;
            }

            @Override
            public boolean makeCall(String userID, int callType) throws RemoteException {
                return makeCallMCOP(userID,callType,context);
            }

            @Override
            public boolean hangUpCall(String sessionID) throws RemoteException {
                return hangUpCallMCOP( sessionID);
            }

            @Override
            public boolean acceptCall(String sessionID) throws RemoteException {
                return acceptCallMCOP(sessionID);
            }

            @Override
            public boolean updateEmergencyState(String sessionID, int callType) throws RemoteException {
                return false;
            }

            @Override
            public boolean floorControlOperation(String sessionID, int requestType, String UserID) throws RemoteException {
                return floorControlOperationMCOP(sessionID,requestType,UserID);
            }

            @Override
            public boolean updateGroupsInfo() throws RemoteException {
                return false;
            }

            @Override
            public boolean updateGroupsAffiliation() throws RemoteException {
                if(BuildConfig.DEBUG)Log.d(TAG,"Execute updateGroupsAffiliation");
                return newPresence();
            }

            @Override
            public boolean groupAffiliationOperation(String groupMcpttId, int affiliationOperation) throws RemoteException {
                return newOperationAffiliation(groupMcpttId,ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum.fromInt(affiliationOperation));
            }

            @Override
            public boolean changeSelectedContact(String groupID) throws RemoteException {
                return false;
            }

            @Override
            public boolean registerCallback(IMCOPCallback mcopCallBack) throws RemoteException {
                if(BuildConfig.DEBUG)Log.d(TAG,"Execute registerCallback");
                boolean result=false;
                if(mcopCallBack!=null){
                    mMCOPCallback=mcopCallBack;
                    result=true;
                }else{
                    Log.e(TAG,"CallBack Error");
                }
                return result;
            }
        };

        String callingApp = context.getPackageManager().getNameForUid(Binder.getCallingUid());
        if(BuildConfig.DEBUG)Log.d(TAG,"callingApp: "+callingApp+"\nuid: "+Binder.getCallingUid() );

        return iBinder;
    }

    private void affiliationImplicitGroups(){
    }

    public boolean stopManagerClient(){
        if(BuildConfig.DEBUG)Log.d(TAG, "stopManagerClient");
        engineIapi.stop();
        if(managerSessions!=null)
        managerSessions.stopManagerSessions();
        if (mSipBroadcastRecvRegister != null) {
            if(BuildConfig.DEBUG)Log.d(TAG, "unregisterReceiver: Register");
            context.unregisterReceiver(mSipBroadcastRecvRegister);
            mSipBroadcastRecvRegister=null;
        }
        if(ngnSipService!=null && ngnSipService.isRegistered())
        return ngnSipService.unRegister();
        return true;
    }

    public boolean onDestroyClient(){
        if(BuildConfig.DEBUG)Log.d(TAG, "onDestroyClient");
        if (mSipBroadcastRecvRegister != null) {
            if(BuildConfig.DEBUG)Log.d(TAG, "unregisterReceiver: Register");
            context.unregisterReceiver(mSipBroadcastRecvRegister);
            mSipBroadcastRecvRegister=null;
        }
        if(managerSessions!=null)
            managerSessions.stopManagerSessions();
        ngnEngine.stop();
        engineIapi.isStarted();
        engineIapi.stop();
        return false;
    }



    //Only for testing purposes
    public boolean selectProfileMCOP2(String data){
        if(ngnEngine.getProfilesService()==null || ngnEngine.getProfilesService().getProfilesNames(context)==null){
            Log.e(TAG,"Error in load profiles from MCOP SDK");
            return false;
        }
        for(String names:ngnEngine.getProfilesService().getProfilesNames(context)){
            Log.d(TAG,"User Profile \""+names+"\"");
        }

        boolean result=ngnEngine.getProfilesService().setProfileNow(context,data);
        Log.d(TAG,"Configure Profile \""+data+"\""+" result: "+result);
        return result;
    }


    //START AUTHENTICATION EVENT
    private boolean sendAuthorizationRequestDataEvent(String requestUri, String redirectUri){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.authorizationRequestEvent.toString());
        event.putExtra(ConstantsMCOP.AuthorizationRequestExtras.REQUEST_URI,requestUri);
        event.putExtra(ConstantsMCOP.AuthorizationRequestExtras.REDIRECT_URI,redirectUri);
        return sendEvent(event);
    }

    private boolean sendErrorAuthorizationEvent(Constants.ConstantsErrorMCOP.AuthorizationRequestEventError authorizationRequestEventError){
        if(authorizationRequestEventError==null || mMCOPCallback==null)return false;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.authorizationRequestEvent.toString());
        Log.e(TAG, "LoginEvent Error "+ authorizationRequestEventError.getCode()+": "+ authorizationRequestEventError.getString());

        //Error Code
        event.putExtra(ConstantsMCOP.AuthorizationRequestExtras.ERROR_CODE,authorizationRequestEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.AuthorizationRequestExtras.ERROR_STRING,authorizationRequestEventError.getString());
        return sendEvent(event);
    }
    //END AUTHENTICATION EVENT

    //START LOGIN EVENT
    private boolean sendLoginEvent(boolean isRegisted){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.loginEvent.toString());
        event.putExtra(ConstantsMCOP.LoginEventExtras.SUCCESS,isRegisted);
        if(isRegisted && ngnEngine.getProfilesService().getProfileNow(context)!=null){
            String mcpttID=ngnEngine.getProfilesService().getProfileNow(context).getMcpttId();
            event.putExtra(ConstantsMCOP.LoginEventExtras.MCPTT_ID,mcpttID);
            String displayName=mcpttID;
            event.putExtra(ConstantsMCOP.LoginEventExtras.DISPLAY_NAME,displayName);
        }
        return sendEvent(event);
    }

    private boolean sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError loginEventError){
        if(loginEventError==null || mMCOPCallback==null)return false;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.loginEvent.toString());
        Log.e(TAG, "LoginEvent Error "+ loginEventError.getCode()+": "+ loginEventError.getString());

        //Error Code
        event.putExtra(ConstantsMCOP.LoginEventExtras.ERROR_CODE,loginEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.LoginEventExtras.ERROR_STRING,loginEventError.getString());
        return sendEvent(event);
    }
    //END LOGIN EVENT

    //START LOGOUT EVENT
    private boolean sendUnLoginEvent(boolean isUnRegisted){
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.unLoginEvent.toString());
        event.putExtra(ConstantsMCOP.UnLoginEventExtras.SUCCESS,isUnRegisted);
        return sendEvent(event);
    }

    private boolean sendErrorUnLoginEvent(Constants.ConstantsErrorMCOP.UnLoginEventError unLoginEventError){
        if(unLoginEventError==null || mMCOPCallback==null){
            Log.e(TAG,"mMCOPCallback or unLoginEventError is null");
            return false;
        }
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.unLoginEvent.toString());
        Log.e(TAG, "LoginEvent Error "+ unLoginEventError.getCode()+": "+ unLoginEventError.getString());
        //Error Code
        event.putExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_CODE,unLoginEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.UnLoginEventExtras.ERROR_STRING,unLoginEventError.getString());
        return sendEvent(event);
    }
    //END LOGOUT EVENT

    //START CALL EVENT
    protected boolean makeCallMCOP(String userID, int type, Context context){
        //TODO: Create priority logic for emergency calls
        if(!ngnEngine.isStarted() || !ngnSipService.isRegistered()){
            sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDVII);
            return false;
        }
        return  makeCallMCOPStart( userID,  type
                ,  context);
    }

    protected boolean makeCallMCOPStart(String userID, int type
            , Context context){
        //TODO: Verify that the client has permission to perform the particular call type
        //With type select if valid call type
        Constants.CallEvent.CallTypeValidEnum typeCall=null;
        if(userID==null || userID.trim().isEmpty() || !NgnUriUtils.isValidSipUri(userID)){
            sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDIIII);
        }else if(type<=0 || (typeCall= validationCallType(type))==null){
            sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDI);
        }else{
            NgnAVSession session=null;
            long newID=-1;
            NgnMediaType typeCallNgn=NgnMediaType.None;
            switch (typeCall) {
                case AudioWithoutFloorCtrlPrivate:
                    break;
                case AudioWithoutFloorCtrlPrivateEmergency:
                    break;
                case AudioWithFloorCtrlPrivate:
                    typeCallNgn=NgnMediaType.SessionAudioMCPTT;
                    break;
                case AudioWithFloorCtrlPrivateEmergency:
                    break;
                case AudioWithFloorCtrlPrearrangedGroupEmergency:
                    break;
                case AudioWithFloorCtrlPrearrangedGroupImminentPeril:
                    break;
                case AudioWithFloorCtrlPrearrangedGroup:
                    typeCallNgn=NgnMediaType.SessionAudioGroupMCPTT;
                    if(BuildConfig.DEBUG)Log.d(TAG,"Type call:"+"AudioWithFloorCtrlPrearrangedGroup   " + typeCallNgn.getValue());

                    break;
                case AudioWithFloorCtrlChatGroupEmergency:
                    break;
                case AudioWithFloorCtrlChatGroupImminentPeril:
                    break;
                case AudioWithFloorCtrlChatGroup:
                    break;
                case AudioWithFloorCtrlBroadcastpEmergency:
                    break;
                case AudioWithFloorCtrlBroadcastImminentPeril:
                    break;
                case AudioWithFloorCtrlBroadcast:
                    break;
                case AudioWithFloorCtrlFirstToAnswer:
                    break;
                case AudioWithFloorCtrlPrivateCallCallback:
                    break;
                case AudioWithFloorCtrlRemoteAmbientListening:
                    break;
                case AudioWithFloorCtrlLocalAmbientListening:
                    break;
                case VideoAudioWithFloorCtrlPrivate:
                    break;
                case VideoAudioWithFloorCtrlPrivateEmergency:
                    break;
                case VideoAudioWithFloorCtrlPrearrangedGroupEmergency:
                    break;
                case VideoAudioWithFloorCtrlPrearrangedGroupImminentPeril:
                    break;
                case VideoAudioWithFloorCtrlPrearrangedGroup:
                    break;
                case VideoAudioWithFloorCtrlChatGroupEmergency:
                    break;
                case VideoAudioWithFloorCtrlChatGroupImminentPeril:
                    break;
                case VideoAudioWithFloorCtrlChatGroup:
                    break;
                case VideoAudioWithFloorCtrlBroadcastpEmergency:
                    break;
                case VideoAudioWithFloorCtrlBroadcastImminentPeril:
                    break;
                case VideoAudioWithFloorCtrlBroadcast:
                    break;
                case VideoAudioWithFloorCtrlFirstToAnswer:
                    break;
                case VideoAudioWithFloorCtrlPrivateCallCallback:
                    break;
                case VideoAudioWithFloorCtrlRemoteAmbientListening:
                    break;
                case VideoAudioWithFloorCtrlLocalAmbientListening:
                    break;
                default:
                    Log.e(TAG,"The call type is not properly known");
                    break;
            }
            if(typeCallNgn==null || typeCallNgn==NgnMediaType.None){
                Log.e(TAG,"Session create error");
                sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDI);
            }else{
                session = NgnAVSession.createOutgoingSession(
                        NgnEngine.getInstance().getSipService().getSipStack(),
                        typeCallNgn
                );
                newID=managerSessions.newSession(session);
                if(newID<=0){
                    Log.e(TAG,"Session create error");
                    sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError.CDVI);
                }else{
                    Log.d(TAG,"Init session");
                    session.makeCallMCPTT(
                            NgnUriUtils.makeValidSipUri(userID,context)
                            ,context
                    );
                }
            }

        }
        return false;
    }

    private boolean hangUpCallMCOP(String sessionID){
        if(managerSessions==null){
            return false;
        }
        return managerSessions.hangUpCall(sessionID);
    }

    private boolean acceptCallMCOP(String sessionID){
        if(managerSessions==null){
            return false;
        }
        return managerSessions.acceptCall(sessionID);
    }

    public boolean floorControlOperationMCOP(String sessionID, int requestType, String userID){
        try{
            ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum floorControlOperationTypeEnum=ConstantsMCOP.FloorControlEventExtras.FloorControlOperationTypeEnum.fromInt(requestType);
            if(managerSessions==null){
                return false;
            }
            return managerSessions.floorControlOperation(sessionID,floorControlOperationTypeEnum,userID);
        }catch (Exception ex){

        }
        return false;
    }

    private boolean sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError callEventError){
        return sendErrorCallEvent(callEventError,null);
    }

    private boolean sendErrorCallEvent(Constants.ConstantsErrorMCOP.CallEventError callEventError,String sessionID){
        if(callEventError==null || mMCOPCallback==null)return false;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.callEvent.toString());
        Log.e(TAG, "CallEvent Error "+ callEventError.getCode()+": "+ callEventError.getString());
        if(sessionID!=null && !sessionID.trim().isEmpty())
        event.putExtra(ConstantsMCOP.CallEventExtras.SESSION_ID,sessionID);
        //put eventType ERROR
        event.putExtra(ConstantsMCOP.CallEventExtras.EVENT_TYPE, ConstantsMCOP.CallEventExtras.CallEventEventTypeEnum.ERROR.getValue());
        //put Code Error
        event.putExtra(ConstantsMCOP.CallEventExtras.ERROR_CODE,callEventError.getCode());
        //put String Error
        event.putExtra(ConstantsMCOP.CallEventExtras.ERROR_STRING,callEventError.getString());
        return sendEvent(event);
    }

    @Override
    public void onEvents(List<Intent> events) {
        sendEvents(events);
    }

    private synchronized boolean sendEvents(final List<Intent> events){
        if(events==null || events.isEmpty() || mMCOPCallback==null)return false;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            public void run() {
                try {
                    mMCOPCallback.handleOnEvent(events);
                } catch (RemoteException e) {
                    Log.e(TAG,"Error sending event to client: "+e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG,"Error sending events: "+e.getMessage());
                }
            }
        });
        return true;
    }

    private boolean sendEvent(final Intent event){
        if(event==null )return false;
        List<Intent> events=new ArrayList<>();
        events.add(event);
        return sendEvents(events);
    }


    private void registerNow(){
        Log.d(TAG,"Starting Registration Process");
        if(ngnSipService.isRegistered() || !ngnSipService.register(context)){
            sendErrorLoginEvent(Constants.ConstantsErrorMCOP.LoginEventError.CCVII);
        }
    }



    //START Affiliation Event
    @Override
    public void receiveNewPresence(Presence presence) {
        newPresence(presence);
    }

    @Override
    public void receiveNewPresenceResponse(Presence presence, String pid) {
        newPresence(presence);
    }

    private boolean newPresence(){
        Presence presence=myAffiliationService.getPresenceNow();
        return newPresence(presence);
    }

    private boolean newPresence(Presence presence){
        Log.d(TAG,"New affiliation data received");
        if(presence==null ){
            Log.e(TAG,"Erroneous affiliation data received.");
            return false;
        }

        List<GroupAffiliation> groupAffiliations= Utils.checkPresence(presence,context);
        if(BuildConfig.DEBUG && groupAffiliations!=null)Log.d(TAG,"The groups received in Affiliation:"+groupAffiliations.size());
        return sendGroupAffiliationEvent(groupAffiliations);
    }

    @Override
    public void expireAffiliations(Map<String, String> expires) {
        Presence presence=ngnEngine.getAffiliationService().getPresenceNow();
        String mcpttClientID=null;
        NgnSipPrefrences profile= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(profile!=null)mcpttClientID=profile.getMcpttClientId();
        if(presence.getTuple()!=null && mcpttClientID!=null){
            for(Tuple tuple:presence.getTuple()){
                if(tuple!=null && presence.getTuple().get(0).getStatus()!=null && tuple.getStatus().getAffiliations()!=null && tuple.getId().trim().equals(mcpttClientID)){
                    for(AffiliationType affiliation:presence.getTuple().get(0).getStatus().getAffiliations()){
                        if(expires.get(affiliation.getGroup())!=null){
                            affiliation.setStatus(StatusType.deaffiliating);
                        }
                    }
                }
            }
        }
        newPresence(presence);
    }

    @Override
    public void receiveNewSelfAffiliation(CommandList commandList) {
        //TODO: Failed to develop the situation where another user asks for affiliation
    }

    @Override
    public void startNewServiceAffiliation() {
        //TODO: Possibly no longer needed, but it should be checked
    }

    private boolean newOperationAffiliation(List<String> groupIDs, ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum operationTypeEnum){
        boolean result=false;
        ngnEngine.getProfilesService().getProfileNow(context);
        ArrayList<String> groupsAffiliation=new ArrayList<>();
        ArrayList<String> groupsUnAffiliation=new ArrayList<>();
        if(groupIDs!=null)
        for(String groupID:groupIDs){
            if(groupID!=null && !groupID.trim().isEmpty() && operationTypeEnum!=null){
                Presence presence=null;
                switch (operationTypeEnum) {
                    case Affiliate:
                        //Verify that the group you want to AFFILIATE to is obtained from the CMS
                        presence=myAffiliationService.getPresenceNow();
                        if(presence==null){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVIII,groupID);
                            result=false;
                        }else if(!checkGroupIsExist(ngnEngine.getProfilesService().getProfileNow(context),groupID,context)
                                ){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVII,groupID);
                            result=false;

                        }else if(isAffiliatedGroup(presence,groupID,context)){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CV,groupID);
                            result=false;
                        }else if(isDeaffiliatedGroup(presence,groupID,context) || (!isDeaffiliatingGroup(presence,groupID,context) && !isAffiliatingGroup(presence,groupID,context))){
                            //The group exists
                            myAffiliationService.affiliationGroup(context,groupID);
                            groupsAffiliation.add(groupID);
                            result=true;
                        }else{
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CIX,groupID);
                        }
                        break;
                    case Deaffiliate:
                        //Verify that the group to DEAFFILIATE from was obtained from the CMS
                        presence=myAffiliationService.getPresenceNow();
                        if(presence==null){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVIII,groupID);
                            result=false;
                        }else if(!checkGroupIsExist(ngnEngine.getProfilesService().getProfileNow(context),groupID,context)
                                ){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVII,groupID);
                            result=false;

                        }else if(isDeaffiliatedGroup(presence,groupID,context)){
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CVI,groupID);
                            result=false;
                        }else if(isAffiliatedGroup(presence,groupID,context) || (!isDeaffiliatingGroup(presence,groupID,context) && !isAffiliatingGroup(presence,groupID,context))){
                            //The group exists
                            groupsUnAffiliation.add(groupID);
                            result=true;
                        }else {
                            sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CIX,groupID);
                        }
                        break;
                }
            }else{
                sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError.CII,groupID);
                result=false;
            }
        }
        if(groupsAffiliation!=null && !groupsAffiliation.isEmpty())
            myAffiliationService.affiliationGroups(context,groupsAffiliation);
        if(groupsUnAffiliation!=null && !groupsUnAffiliation.isEmpty())
            myAffiliationService.unAffiliationGroups(context,groupsUnAffiliation);

        return result;
    }

    private boolean newOperationAffiliation(String groupID, ConstantsMCOP.GroupAffiliationEventExtras.AffiliationOperationTypeEnum operationTypeEnum){
        List<String> groupIDs=new ArrayList<>();
        if(groupID!=null);
        groupIDs.add(groupID);
        return newOperationAffiliation(groupIDs,operationTypeEnum);
    }

    private boolean processNewGroup(org.doubango.ngn.datatype.gms.pocListService.ns.list_service.Group group){
        NgnSipPrefrences profile= NgnEngine.getInstance().getProfilesService().getProfileNow(context);
        if(group==null || profile==null || profile.getMcpttId()==null || !profile.getMcpttId().isEmpty()){
            sendErrorGroupInfoEvent(Constants.ConstantsErrorMCOP.GroupInfoEventError.CI);
            return false;
        }
        Map<String,GroupInfo> groupsInfo=new HashMap<>();
        if(group.getListService()!=null)
        for(ListServiceType serviceType:group.getListService()){
            if(serviceType.getUri()!=null && !serviceType.getUri().trim().isEmpty()){

                Set<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowTypes= ManagerClientUtils.getAllowsGroups(profile.getMcpttId(),serviceType);
                Iterator<ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum> allowTypeIterator= allowTypes.iterator();
                ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType actionRealTimeVideo=null;
                while(allowTypeIterator.hasNext()){
                    ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum allowType=allowTypeIterator.next();
                    if(allowType== ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.non_real_time_video_mode){
                        actionRealTimeVideo= ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType.non_real_time;
                    }else if(allowType== ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.non_urgent_real_time_video_mode){
                        actionRealTimeVideo= ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType.non_urgent_real_time;
                    }else if(allowType== ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.urgent_real_time_video_mode){
                        actionRealTimeVideo= ConstantsMCOP.GroupInfoEventExtras.ActionRealTimeVideoType.urgent_real_time;
                    }
                }

                GroupInfo groupInfo=new GroupInfo(
                        serviceType.getUri(),
                        serviceType.getDisplayName().getValue(),
                        allowTypes,
                        actionRealTimeVideo,//TODO: Maybe not correct
                        serviceType.getMcdataonnetworkmaxdatasizeforSDS(),
                        serviceType.getMcdataonnetworkmaxdatasizeforFD(),
                        serviceType.getMcdataonnetworkmaxdatasizeautorecv(),
                        ManagerClientUtils.getParticipantGroups(serviceType)
                );
                groupsInfo.put(serviceType.getUri(),groupInfo);
            }
        }
        return true;
    }

    private boolean sendGroupAffiliationEvent(List<GroupAffiliation> groupAffiliations){
        if(BuildConfig.DEBUG)Log.d(TAG,"sendGroupAffiliationEvent");
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupAffiliationEvent.toString());
        HashMap<String, Integer> stringIntegerHashMap=(HashMap<String, Integer>)Utils.groupAffiliationToMap(groupAffiliations);
        if(BuildConfig.DEBUG)Log.d(TAG,"stringIntegerHashMap size: "+stringIntegerHashMap.size());
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.EVENT_TYPE, ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationEventTypeEnum.GROUP_AFFILIATION_UPDATE.getValue());
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.GROUPS_LIST,stringIntegerHashMap);
        return sendEvent(event);
    }

    private boolean sendErrorGroupAffiliationEvent(Constants.ConstantsErrorMCOP.GroupAffiliationEventError  groupAffiliationEventError ,String groupID){
        if(groupAffiliationEventError==null || mMCOPCallback==null )return false;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupAffiliationEvent.toString());
        Log.e(TAG, "GroupAffiliationEvent Error "+ groupAffiliationEventError.getCode()+": "+ groupAffiliationEventError.getString());
        //Error Group ID
        if(groupID!=null && !groupID.trim().isEmpty())
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.GROUP_ID,groupID);
        //Event Type
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.EVENT_TYPE,ConstantsMCOP.GroupAffiliationEventExtras.GroupAffiliationEventTypeEnum.GROUP_AFFILIATION_ERROR.getValue());
        //Error Code
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.ERROR_CODE, groupAffiliationEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.GroupAffiliationEventExtras.ERROR_STRING, groupAffiliationEventError.getString());
        return sendEvent(event);
    }
    //END Affiliation Event

    //START Group Info Event
    private boolean sendGroupInfoEvent(List<GroupInfo> groupsInfo){
        boolean result=true;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupInfoEvent.toString());
        for(GroupInfo groupInfo:groupsInfo)
            if(groupInfo!=null){
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.GROUP_ID,
                        groupInfo.getGroupID());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.DISPLAY_NAME,
                        groupInfo.getDisplayName());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ALLOWS_GROUP,
                        ConstantsMCOP.GroupInfoEventExtras.AllowTypeEnum.getValue(groupInfo.getAllowList()));
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.MAX_DATA_SIZE_FOR_SDS,
                        groupInfo.getMaxDataSizeForSDS());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.MAX_DATA_SIZE_FOR_FD,
                        groupInfo.getMaxDataSizeForFD());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.MAX_DATA_SIZE_AUTO_RECV,
                        groupInfo.getMaxDataSizeAutoRecv());
                event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ACTIVE_REAL_TIME_VIDEO_MODE,
                        groupInfo.getActionRealTimeVideo().toString());
                result&=sendEvent(event);

            }
        return result;
    }

    private boolean sendErrorGroupInfoEvent(Constants.ConstantsErrorMCOP.GroupInfoEventError  groupInfoEventError){
        if(groupInfoEventError==null || mMCOPCallback==null )return false;
        Intent event=new Intent(ConstantsMCOP.ActionsCallBack.groupInfoEvent.toString());
        Log.e(TAG, "GroupInfoEvent Error "+ groupInfoEventError.getCode()+": "+ groupInfoEventError.getString());

        //Error Code
        event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ERROR_CODE, groupInfoEventError.getCode());
        //Error String
        event.putExtra(ConstantsMCOP.GroupInfoEventExtras.ERROR_STRING, groupInfoEventError.getString());
        return sendEvent(event);
    }
    //END org.doubango.ngn.datatype.gms.pocListService.ns.list_service.Group Info Event

    //START SIM AUTH
    @Override
    public String onAuthRegister(String nonce) {
        try {
            //TODO: Should be decided as and where it decides to use a slot of SIM or an algorithm specific CHECK
            String response = engineIapi.getSimService().getAuthentication( nonce.trim());
            return response;
        } catch (RemoteException e) {
            Log.e(TAG,"SIM Authorization Error: "+e.getMessage());
        }
        return null;
    }
    //END SIM AUTH
}