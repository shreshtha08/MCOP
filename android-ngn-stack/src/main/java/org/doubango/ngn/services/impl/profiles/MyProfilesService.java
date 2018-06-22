/*
*  Copyright (C) 2017 Eduardo Zarate Lasurtegui
*  Copyright (C) 2017, University of the Basque Country (UPV/EHU)
*
* Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* This file is part of MCOP MCPTT Client
*
* This is free software: you can redistribute it and/or modify it under the terms of
* the GNU General Public License as published by the Free Software Foundation, either version 3
* of the License, or (at your option) any later version.
*
* This is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
* without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
* See the GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License along
* with this program; if not, write to the Free Software Foundation, Inc.,
* 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/
package org.doubango.ngn.services.impl.profiles;

import android.content.Context;
import android.util.Log;

import org.doubango.ngn.NgnEngine;
import org.doubango.ngn.datatype.profiles.Profiles;
import org.doubango.ngn.services.impl.preference.PreferencesManager;
import org.doubango.ngn.services.preference.IPreferencesManager;
import org.doubango.ngn.services.profiles.IMyProfilesService;
import org.doubango.ngn.sip.NgnSipPrefrences;
import org.doubango.ngn.utils.NgnConfigurationEntry;
import org.doubango.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class MyProfilesService implements IMyProfilesService {
    private final static String TAG = Utils.getTAG(MyProfilesService.class.getCanonicalName());
    private static Profiles profilesNow;
    private static Map<String,NgnSipPrefrences> profilesMap;
    private static NgnSipPrefrences profileNow;
    private static IPreferencesManager preferencesManager;
    private OnSetProfileListener mOnSetProfileListener;
    private static final String IMPORTED_PROFILES_SAVED="IMPORTED_PROFILES_SAVED"+TAG;

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    private boolean readProfile(Context context){
        try {
            if(context==null)return false;
            if(profilesNow==null)profilesNow=new Profiles();
            profilesNow=getProfiles(context);
            if(profilesNow==null)return false;
            if(profilesMap==null)profilesMap=new TreeMap<>();
            for(NgnSipPrefrences profile:profilesNow.getProfiles()){
                if(profile.getName()!=null){
                    profilesMap.put(profile.getName(),profile);
                }
            }
            return true;
        }catch (NoSuchMethodException e) {
            Log.e(TAG,"Error reading profiles. No access method:"+e.toString());
        } catch (Exception e1) {
            Log.e(TAG,"Error reading profiles:"+e1.toString());
        }
        return false;
    }

    public NgnSipPrefrences getProfile(Context context, String name,boolean forceReadPofile){
        if(profilesNow==null || profilesNow.isEmpty() || forceReadPofile){
            if(!readProfile(context))return null;
        }
        if(profilesMap!=null){
            return profilesMap.get(name);
        }
        return null;
    }

    public ArrayList<String> getProfilesNames(Context context){
        if(profilesNow==null || profilesNow.isEmpty()){
            if(!readProfile(context))return null;
        }
        if(profilesMap!=null){
            String[] strings= profilesMap.keySet().toArray(new String[profilesMap.keySet().size()]);
            return new ArrayList<>(Arrays.asList(strings));
        }
        return null;
    }

    public boolean setProfileNow(Context context,String nameProfile){
        if(nameProfile==null)return false;
        if(profilesMap==null)return false;
        NgnSipPrefrences profileNow=profilesMap.get(nameProfile);
        if(profileNow==null)return false;
        if(MyProfilesService.profileNow==null) MyProfilesService.profileNow =profileNow;
        if(MyProfilesService.profileNow.getName()!=null && profileNow.getName()!=null) {
            if (MyProfilesService.profileNow.getName().compareTo(profileNow.getName()) == 0) {
                Log.d(TAG, "The same profile is selected");
            } else {

                Log.d(TAG, "The same profile isn´t selected");
            }
        }
        MyProfilesService.profileNow=profileNow;
        NgnEngine.getInstance().getConfigurationService().putString(NgnConfigurationEntry.PROFILE_USE,nameProfile);
        NgnEngine.getInstance().getConfigurationService().commit();
        //This is used by NGNsipService to load data from default profile.
        if(mOnSetProfileListener!=null)mOnSetProfileListener.onSetProfile();
        return true;
    }

    public void setProfileNow(NgnSipPrefrences profileNow){
        if(profileNow==null)return;
        this.profileNow=profileNow;
        return;
    }
    public NgnSipPrefrences getProfileNow(Context context){
        if(profilesNow==null){
            String name=NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.PROFILE_USE,NgnConfigurationEntry.DEFAULT_PROFILE_USE);
            if(NgnConfigurationEntry.DEFAULT_PROFILE_USE.compareTo(name)!=0){
                profileNow=getProfile(context,name,false);
            }

        }

        return profileNow;
    }

    public boolean invalidProfile(Context context){
        if(profilesNow!=null){
            String name=NgnEngine.getInstance().getConfigurationService().getString(NgnConfigurationEntry.PROFILE_USE,NgnConfigurationEntry.DEFAULT_PROFILE_USE);
            if(!NgnConfigurationEntry.DEFAULT_PROFILE_USE.equals(name)){
                profileNow=getProfile(context,name,true);
                if(profileNow!=null)return true;
            }
        }

        return false;
    }
    //Used by NGNsipService to load data from default profile.


    public void setOnSetProfileListener(OnSetProfileListener mOnSetProfileListener){
        this.mOnSetProfileListener=mOnSetProfileListener;
    }

    public void setProfiles(List<NgnSipPrefrences> profiles) {
        if(profiles!=null && !profiles.isEmpty()){
            if(profilesNow!=null){

            }else{
                profilesNow=new Profiles();
            }
            profilesNow.setProfiles(profiles);
        }
    }

    public boolean importProfiles(String profiles,Context context){
        if(profiles==null || profiles.isEmpty())return false;
        try {
            Profiles profilesNews=ProfilesUtils.getProfiles(profiles);
            if(profilesNews!=null && !profilesNews.isEmpty()){
                return saveProfiles(profilesNews,context);
            }
        } catch (Exception e) {
            Log.e(TAG,"Error importing profiles "+e.toString());
        }
        return false;
    }

    private  boolean saveProfiles(Profiles profiles, Context context){
        if(profiles!=null && !profiles.isEmpty()){

            preferencesManager=new PreferencesManager(IMPORTED_PROFILES_SAVED);
            try {
                if(preferencesManager.putString(context,IMPORTED_PROFILES_SAVED,ProfilesUtils.getStringOfProfiles(context,profiles))){
                    profilesNow=null;
                    profilesMap=null;
                    return true;
                }
            } catch (Exception e) {
                Log.e(TAG,"Error importing profiles "+e.toString());
            }
            return false;
        }
        return false;
    }

    protected static Profiles getProfiles(Context context) throws Exception {
        if(context==null)return null;
        preferencesManager=new PreferencesManager(IMPORTED_PROFILES_SAVED);
        String profiles=preferencesManager.getString(context,IMPORTED_PROFILES_SAVED);
        if(profiles!=null && !profiles.isEmpty() && !profiles.equalsIgnoreCase(PreferencesManager.STRING_DEFAULT)){
            Log.w(TAG,"Device has imported profiles");
            return ProfilesUtils.getProfiles(profiles);
        }else{
            Log.w(TAG,"Device doesn´t have imported profiles.");
        }
        return ProfilesUtils.getProfiles(context);
    }
    @Override
    public boolean clearService(){
        return true;
    }
}
