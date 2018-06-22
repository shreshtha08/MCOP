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

interface IConfigurationService {

    /*** Error handling ********************************************************/

    /**
     * Get current error code. After an MCOP service function returns an error,
     * you can use this to query for the exact error reason.
     *
     * @return  An integer error code
     * @see     Constants.Common.Error
     * @see     Constants.Configuration.Error
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
     * @see     Constants.Configuration.Capabilities
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
     * @see     Constants.Configuration.Capabilities
     */
    String[] checkCapabilityList(int cap);


    /*** Configuration / provisioning data access ******************************/

    /**
     * Read a configuration file from the SIM or from the ME (Mobile Equipment).
     *
     * @param   storage configuration file storage
     * @param   type    configuration file type
     * @return  an array of byte data, or null if not present
     * @see     Constants.Configuration.Storage
     * @see     Constants.Configuration.FileType
     */
    byte[] readConfigurationFile(int storage, int type);

    /**
     * Write a configuration file to the SIM or to the ME.
     *
     * @param   storage configuration file storage
     * @param   type    configuration file type
     * @param   data    an array of byte data to write
     * @return  true if write was successful, false otherwise
     * @see     Constants.Configuration.Storage
     * @see     Constants.Configuration.FileType
     */
    boolean writeConfigurationFile(int storage, int type, in byte[] data);


    /*** Security and authentication *******************************************/

    /**
     * Check if the given package is trusted by ConfigurationService.
     *
     * @param  pkgname Full name of the package
     * @return true if trusted, false otherwise
     */
    boolean checkPackageAuth(String pkgname);

    /**
     * Check if the given UID is trusted by ConfigurationService.
     *
     * @param  uid UID to check
     * @return true if trusted, false otherwise
     */
    boolean checkUidAuth(int uid);
}
