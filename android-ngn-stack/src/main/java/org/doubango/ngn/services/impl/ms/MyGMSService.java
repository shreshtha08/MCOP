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



package org.doubango.ngn.services.impl.ms;

import org.doubango.ngn.datatype.gms.pocListService.ns.list_service.Group;
import org.doubango.ngn.services.gms.IMyGMSService;
import org.doubango.utils.Utils;


public class MyGMSService implements IMyGMSService {

    private final static String TAG = Utils.getTAG(MyGMSService.class.getCanonicalName());

    private static boolean isStart;
    private static Group currentGroups;
    private OnGMSListener onGMSListener;


    public MyGMSService() {
        currentGroups=null;
        isStart=false;
    }

    @Override
    public boolean start() {
        currentGroups=null;
        isStart=true;
        return true;
    }

    @Override
    public boolean stop() {
        isStart=false;
        currentGroups=null;
        return true;
    }

    @Override
    public boolean clearService() {
        return false;
    }


    @Override
    public void setOnGMSListener(OnGMSListener onGMSListener) {
        this.onGMSListener=onGMSListener;
    }

    @Override
    public void newGroupData(String groupData){
        try {
            Group newGroup=GMSUtils.getGroupConfiguration(groupData);
            if(newGroup!=null){
                this.currentGroups=newGroup;
                //Send update from grops
                if(onGMSListener!=null)onGMSListener.onGMSNewGroup(this.currentGroups);
            }
        } catch (Exception e) {
            if(onGMSListener!=null)onGMSListener.onGMSErrorNewGroup("The new group is not correct");
        }



    }
    @Override
    public Group getCurrentGroups(){
        return this.currentGroups;
    }

}
