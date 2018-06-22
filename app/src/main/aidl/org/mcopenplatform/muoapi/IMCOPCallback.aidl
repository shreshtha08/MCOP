/*
 *  Copyright (C) 2018 Eduardo Zarate Lasurtegui
 *  Copyright (C) 2018, University of the Basque Country (UPV/EHU)
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
package org.mcopenplatform.muoapi;

/**
 * AIDL definition {@link https://developer.android.com/guide/components/aidl.html}
 * Used as a callback for MCOP SDK server-client communication, and for MCPTT (Mission Critical Push to Talk) Services.
 * @author Eduardo Zarate Lasurtegui
 * @version 0.1
 */
interface IMCOPCallback {
    /**
     *
     * Callback to be listened to by the client. It provides active events, responses to methods
     * executed by the client, asynchronous events from the MCPTT system, and errors produced.
     *
     * @return
     * @see org.mcopenplatform.muoapi.ConstantsMCOP.ActionsCallBack org.mcopenplatform.muoapi.ConstantsMCOP.ActionsCallBack (Types of actions that each of the callback events can have)
     * @param actionList Intent list. Each component in the list contains an event.
     */
    void handleOnEvent(in List<Intent> actionList);
}