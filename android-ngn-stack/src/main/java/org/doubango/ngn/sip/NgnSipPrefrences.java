/*
* Copyright (C) 2017 Eduardo Zarate Lasurtegui
* Copyright (C) 2017, University of the Basque Country (UPV/EHU)
*  Contact for licensing options: <licensing-mcpttclient(at)mcopenplatform(dot)com>
*
* The original file was part of Open Source IMSDROID
*  Copyright (C) 2010-2011, Mamadou Diop.
*  Copyright (C) 2011, Doubango Telecom.
*
*
* Contact: Mamadou Diop <diopmamadou(at)doubango(dot)org>
*
* This file is part of Open Source Doubango Framework.
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
package org.doubango.ngn.sip;


import android.net.Uri;
import android.support.annotation.NonNull;

import org.doubango.utils.Utils;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


@Root(strict=false, name = "NgnSipPrefrences")
public class NgnSipPrefrences {
	private final static String TAG = Utils.getTAG(NgnSipPrefrences.class.getCanonicalName());

	@Element(required = false , name = "DisplayName")
	private String displayName;

	@Attribute(required = false,name = "name") private String name;
	@Element(name="Password" ,required = false) 	private String mPassword;
	@Element(name="Presence" ,required = false) 	private Boolean mPresence;
	@Element(name="XcapEnabled" ,required = false) 	private Boolean mXcapEnabled;
	@Element(name="PresenceRLS" ,required = false) 	private Boolean mPresenceRLS;
	@Element(name="PresencePub" ,required = false) 	private Boolean mPresencePub;
    @Element(name="PresenceSub" ,required = false) 	private Boolean mPresenceSub;
    @Element(name="MWI" ,required = false) 	private Boolean mMWI;
    @Element(name="IMPI" ,required = false) 	private String mIMPI;
    @Element(name="IMPU" ,required = false) 	private String mIMPU;
    @Element(name="Realm" ,required = false) 	private String mRealm;
    @Element(name="PcscfHost" ,required = false) 	private String mPcscfHost;
    @Element(name="PcscfPort" ,required = false) 	private int mPcscfPort=-1;
    @Element(name="Transport" ,required = false) 	private String mTransport;
    @Element(name="IPVersion" ,required = false) 	private String mIPVersion;
    @Element(name="IPsecSecAgree" ,required = false) 	private Boolean mIPsecSecAgree;
    @Element(name="LocalIP" ,required = false) 	private String mLocalIP;
    @Element(name="HackAoR" ,required = false) 	private Boolean mHackAoR;


	@Element(name="McpttPsiCallPrivate" ,required = false) 	private String mMcpttPsiCallPrivate;
	@Element(name="McpttPsiCallGroup" ,required = false) 	private String mMcpttPsiCallGroup;
	@Element(name="McpttPsiCallPreestablished" ,required = false) 	private String mMcpttPsiCallPreestablished;

	@Element(name="McpttId" ,required = false) 	private String mMcpttId;
	@Element(name="McpttClientId" ,required = false) 	private String mMcpttClientId;
	@Element(name="McpttPriority" ,required = false) 	private int mMcpttPriority=-1;
	@Element(name="McpttImplicit" ,required = false) 	private Boolean mMcpttImplicit;
	@Element(name="McpttGranted" ,required = false) 	private Boolean mMcpttGranted;
	@Element(name="McpttPrivAnswerMode" ,required = false) 	private Boolean mMcpttPrivAnswerMode;
	@Element(name="McpttAnswerMode" ,required = false) 	private Boolean mMcpttAnswerMode;
	@Element(name="McpttNameSpace" ,required = false) 	private Boolean mMcpttNameSpace;
	@Element(name="McpttPsiAffiliation" ,required = false) 	private String mMcpttPsiAffiliation;
	@Element(name="McpttPsiAuthentication" ,required = false) 	private String mMcpttPsiAuthentication;
	@Element(name="McpttIsEnableAffiliation" ,required = false) 	private Boolean mMcpttIsEnableAffiliation;
	@Element(name="McpttIsSelfAffiliation" ,required = false) 	private Boolean mMcpttIsSelfAffiliation;


	@Element(name="McpttLocationInfoVersionOld" ,required = false) 	private Boolean mMcpttLocationInfoVersionOld;
	@Element(name="McpttEnableMbms" ,required = false) 	private Boolean mMcpttEnableMbms;


	//GUI
	@Element(name="McpttPlayerSound" ,required = false) private Boolean mMcpttPlayerSound;
	@Element(name="McpttSelfRegistration" ,required = false) private Boolean mMcpttSelfRegistration;//MCPTT_SELF_REGISTRATION

	public NgnSipPrefrences(){
    	
    }




	public Boolean isMcpttPlayerSound() {
		return mMcpttPlayerSound;
	}

	public void setMcpttPlayerSound(Boolean mMcpttPlayerSound) {
		this.mMcpttPlayerSound = mMcpttPlayerSound;
	}


	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}



	public Boolean isMcpttEnableMbms() {
		return mMcpttEnableMbms;
	}

	public void setMcpttEnableMbms(Boolean mMcpttEnableMbms) {
		this.mMcpttEnableMbms = mMcpttEnableMbms;
	}


	public Boolean isMcpttLocationInfoVersionOld() {
		return mMcpttLocationInfoVersionOld;
	}

	public void setMcpttLocationInfoVersionOld(Boolean mMcpttLocationInfoVersionOld) {
		this.mMcpttLocationInfoVersionOld = mMcpttLocationInfoVersionOld;
	}
	public Boolean isMcpttIsSelfAffiliation() {
		return mMcpttIsSelfAffiliation;
	}

	public void setMcpttIsSelfAffiliation(Boolean mMcpttIsSelfAffiliation) {
		this.mMcpttIsSelfAffiliation = mMcpttIsSelfAffiliation;
	}

	public String getPassword() {
		return mPassword;
	}

	public void setPassword(String pass) {
		this.mPassword = pass;
	}

	public String getMcpttPsiCallPrivate() {
		return mMcpttPsiCallPrivate;
	}

	public void setMcpttPsiCallPrivate(String mMcpttPsiCallPrivate) {
		this.mMcpttPsiCallPrivate = mMcpttPsiCallPrivate;
	}

	public String getMcpttPsiCallGroup() {
		return mMcpttPsiCallGroup;
	}

	public void setMcpttPsiCallGroup(String mMcpttPsiCallGroup) {
		this.mMcpttPsiCallGroup = mMcpttPsiCallGroup;
	}

	public String getMcpttPsiCallPreestablished() {
		return mMcpttPsiCallPreestablished;
	}

	public void setMcpttPsiCallPreestablished(String mMcpttPsiCallPreestablished) {
		this.mMcpttPsiCallPreestablished = mMcpttPsiCallPreestablished;
	}
	public Boolean isMcpttIsEnableAffiliation() {
		return mMcpttIsEnableAffiliation;
	}

	public void setMcpttIsEnableAffiliation(Boolean mcpttIsEnableAffiliation) {
		this.mMcpttIsEnableAffiliation = mcpttIsEnableAffiliation;
	}

	public String getMcpttPsiAffiliation() {
		return mMcpttPsiAffiliation;
	}


	public void setMcpttPsiAffiliation(String mMcpttPsiAffiliation) {
		this.mMcpttPsiAffiliation = mMcpttPsiAffiliation;
	}

	public String getMcpttId() {
		return mMcpttId;
	}

	public void setMcpttId(String mMcpttId) {
		this.mMcpttId = mMcpttId;
	}

	public int getMcpttPriority() {
		return mMcpttPriority;
	}

	public void setMcpttPriority(int mMcpttPriority) {
		this.mMcpttPriority = mMcpttPriority;
	}

	public Boolean isMcpttImplicit() {
		return mMcpttImplicit;
	}

	public void setMcpttImplicit(Boolean mMcpttImplicit) {
		this.mMcpttImplicit = mMcpttImplicit;
	}

	public Boolean isMcpttGranted() {
		return mMcpttGranted;
	}

	public void setMcpttGranted(Boolean mMcpttGranted) {
		this.mMcpttGranted = mMcpttGranted;
	}




	public Boolean isMcpttPrivAnswerMode() {
		return mMcpttPrivAnswerMode;
	}

	public void setMcpttPrivAnswerMode(Boolean mMcpttPrivAnswerMode) {
		this.mMcpttPrivAnswerMode = mMcpttPrivAnswerMode;
	}

	public Boolean isMcpttAnswerMode() {
		return mMcpttAnswerMode;
	}

	public String getMcpttClientId() {
		return mMcpttClientId;
	}

	public void setMcpttClientId(String mcpttClientId) {
		this.mMcpttClientId = mcpttClientId;
	}

	public Boolean isMcpttNameSpace() {
		return mMcpttNameSpace;
	}

	public void setMcpttNameSpace(Boolean mMcpttNameSpace) {
		this.mMcpttNameSpace = mMcpttNameSpace;
	}

	public void setMcpttAnswerMode(Boolean mMcpttAnswerMode) {
		this.mMcpttAnswerMode = mMcpttAnswerMode;
	}



    
    public void setPresenceEnabled(Boolean enabled) {
		this.mPresence = enabled;
	}

	public Boolean isPresenceEnabled() {
		return mPresence;
	}
	
	public void setXcapEnabled(Boolean xcapEnabled) {
		this.mXcapEnabled = xcapEnabled;
	}
	
	public Boolean isXcapEnabled() {
		return mXcapEnabled;
	}
	
	public void setPresenceRLS(Boolean presenceRLS) {
		this.mPresenceRLS = presenceRLS;
	}
	
	public Boolean isPresenceRLS() {
		return mPresenceRLS;
	}
	
	public void setMWI(Boolean MWI) {
		this.mMWI = MWI;
	}
	
	public Boolean isMWI() {
		return mMWI;
	}
	
	public void setPresencePub(Boolean presencePub) {
		this.mPresencePub = presencePub;
	}
	
	public Boolean isPresencePub() {
		return mPresencePub;
	}
	
	public void setIMPI(String IMPI) {
		this.mIMPI = IMPI;
	}
	
	public String getIMPI() {
		return mIMPI;
	}
	
	public void setIMPU(String IMPU) {
		this.mIMPU = IMPU;
	}
	
	public String getIMPU() {
		return mIMPU;
	}
	
	public void setRealm(String realm) {
		if((mRealm = realm) != null){
			if(!mRealm.contains(":")){
				mRealm="sip:"+mRealm;
			}
		}
	}
	
	public String getRealm() {
		return mRealm;
	}
	public String getRealmWhitoutProtocol() {
		if(mRealm!=null){
			if(mRealm.compareToIgnoreCase("sip:")==0){
				Uri realmUri;
				if((realmUri=Uri.parse(mRealm))!=null){
					return realmUri.getHost();
				}
			}else{
				return mRealm;
			}

		}
		return null;
	}
	
	public void setPresenceSub(Boolean presenceSub) {
		this.mPresenceSub = presenceSub;
	}
	
	public Boolean isPresenceSub() {
		return mPresenceSub;
	}
	
	
	public void setPcscfHost(String pcscfHost) {
		this.mPcscfHost = pcscfHost;
	}
	
	public String getPcscfHost() {
		return mPcscfHost;
	}
	
	public void setPcscfPort(int pcscfPort) {
		this.mPcscfPort = pcscfPort;
	}
	
	public int getPcscfPort() {
		return mPcscfPort;
	}
	
	public void setIPVersion(String IPVersion) {
		this.mIPVersion = IPVersion;
	}
	
	public String getIPVersion() {
		return mIPVersion;
	}
	
	public void setTransport(String mTransport) {
		this.mTransport = mTransport;
	}
	public String getTransport() {
		return mTransport;
	}
	
	public void setIPsecSecAgree(Boolean IPsecSecAgree) {
		this.mIPsecSecAgree = IPsecSecAgree;
	}
	
	public Boolean isIPsecSecAgree() {
		return mIPsecSecAgree;
	}

	public void setLocalIP(String localIP) {
		this.mLocalIP = localIP;
	}

	public String getLocalIP() {
		return mLocalIP;
	}

	public void setHackAoR(Boolean mHackAoR) {
		this.mHackAoR = mHackAoR;
	}

	public Boolean isHackAoR() {
		return mHackAoR;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}



	@Override
	public boolean equals(@NonNull Object other){

		if (other!=null && other==this)return true;
		if (!(other instanceof NgnSipPrefrences))return false;
		return super.equals(other);


	}

}
