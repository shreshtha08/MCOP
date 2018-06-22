/*
Copyright 2018 Bittium Wireless Ltd.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions
are met:
- Redistributions of source code must retain the above copyright
  notice, this list of conditions and the following disclaimer.
- Redistributions in binary form must reproduce the above copyright
  notice, this list of conditions and the following disclaimer in the
  documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package org.mcopenplatform.iapi;

import org.mcopenplatform.iapi.McopMessenger;

interface IConnectivityService {

    /*** Error handling ********************************************************/

    /**
     * Get current error code. After an MCOP service function returns an error,
     * you can use this to query for the exact error reason.
     *
     * @return  An integer error code
     * @see     Constants.Common.Error
     * @see     Constants.Connectivity.Error
     */
    int getErrorCode();

    /**
     * Get a string representation of the current error. Useful for debugging,
     * but note that this string may not be localized for the user.
     *
     * @return  A string describing the last error
     */
    String getErrorStr();


    /*** Notifications *********************************************************/


    /**
     * Register a messenger instance to receive notifications from the MCOP
     * service. Note that you may receive a notification even while this function
     * is still executing.
     *
     * <p><b>This function is platform-specific.</b> On Android, McopMessenger
     * is just a wrapped instance of Messenger. You must do the relevant setup to
     * receive messages yourself.
     *
     * @param m  Messenger instance to receive the notifications
     */
    void registerNotificationReceiver(in McopMessenger m);


    /*** Capability checks *****************************************************/


    /**
     * Check the service implementation for a specific capability. This
     * version checks for on/off and integer types.
     *
     * @param   cap  Capability to check.
     * @return  capability value, or -1 if the capability is invalid;
     *          for boolean capabilities 0 for unsupported, 1 for supported
     * @see     Constants.Connectivity.Capabilities
     */
    int checkCapability(int cap);

    /**
     * Check the service implementation for a specific capability. This
     * version checks for list type capabilities (constants starting with
     * LIST). If the capability is completely unsupported an empty list
     * will be returned.
     *
     * @param   cap  Capability to check.
     * @return  list describing the requested capability, an empty list if
     *          unsupported, or null if the capability is invalid
     * @see     Constants.Connectivity.Capabilities
     */
    String[] checkCapabilityList(int cap);


    /*** Connectivity **********************************************************/


    /**
     * Create a MC PTT APN. If the APN can not be created for any reason
     * an error is returned. You can query the list of acceptable APN
     * types via checkCapabilityList.
     *
     * <p>If your program crashes or for some other reason disconnects from the
     * underlying MCOP service all created APNs will be deleted automatically.
     *
     * <p>Note that this call may take some time, so do not call from a thread
     * that can not afford to block.
     *
     * @param  name  Name of the new APN
     * @param  type  Type of the new APN, f.ex. "ims"
     * @return       true if creation was successful, false otherwise
     */
    boolean createAPN(String name, String type);

    /**
     * Delete a previously created MC PTT APN. You can only delete APNs that
     * you previously have created via createAPN.
     *
     * @param  name  Name of APN to delete
     * @return       true if APN was deleted, false otherwise
     */
    boolean deleteAPN(String name);

    /**
     * Activate a previously created MC PTT APN. After this call returns
     * successfully the APN will be fully established and ready to carry data.
     *
     * @param  name  Name of the APN to activate
     * @return       true if APN was succesfully activated, false otherwise
     */
    boolean activateAPN(String name);

    /**
     * Control what non-MC calls are allowed. If your application disconnects
     * from the MCOP service the state is automatically set to NONE.
     *
     * @param block  One of the BLOCK_CALLS integer constants.
     * @see   Constants.Connectivity.CallBlock
     */
    void blockNonMCCalls(int block);
}
