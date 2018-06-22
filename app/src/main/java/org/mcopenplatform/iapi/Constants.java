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

public class Constants {
    private Constants() { }

    public class Common {
        private Common() { }

        /** Constants for errors common to all plugins / services */
        public class Error {
            private Error() { }

            /** No error / success */
            static public final int NO_ERROR = 0;

            /** Capability is unknown, or used for incorrect type (int vs list) */
            static public final int UNKNOWN_CAPABILITY = 10001;

            /** Permission denied */
            static public final int PERMISSION_DENIED = 10002;

            /** User authentication failed. Your app does not have carrier privileges
                and/or you could not be authenticated against the internal whitelist. */
            static public final int AUTH_FAILED = 10003;

            /** Invalid parameter */
            static public final int INVALID_PARAMETER = 10004;

            /** Internal error */
            static public final int INTERNAL_ERROR = 10005;
        }
    }

    public class Connectivity {
        private Connectivity() { }

        /** Constants for Connectivity capabilities */
        public class Capability {
            private Capability() { }

            /** List of supported MC APN types */
            static public final int LIST_MC_APN_TYPES = 0;

            /** List of supported QCI classes */
            static public final int LIST_QCI_CLASSES = 1;
        }

        /** Constants for getErrorCode */
        public class Error {
            private Error() { }

        }

        /** Constants for blocking non-MC calls */
        public class CallBlock {
            private CallBlock() { }

            /** Do not block any calls */
            static public final int NONE = 0;

            /** Block incoming (MT) non-MC calls */
            static public final int INCOMING = 1;

            /** Block outgoing (MO) non-MC calls */
            static public final int OUTGOING = 2;

            /** Block all non-MC calls */
            static public final int ALL = 3;
        }
    }

    public class Sim {
        private Sim() { }

        /** Constants for SIM capabilities */
        public class Capability {
            private Capability() { }
        }

        /** Constants for getErrorCode */
        public class Error {
            private Error() { }
        }

        /** Constants for SIM slot id's */
        public class SimSlot {
            private SimSlot() { }

            /** SIM which is default subscription */
            static public final int SLOT_ID_DEFAULT = 0;

            /** SIM slot ID 1 */
            static public final int SLOT_ID_1 = 1;

            /** SIM slot ID 2 */
            static public final int SLOT_ID_2 = 2;
        }

        /** Constants for SIM application types */
        public class SimApp {
            private SimApp() { }

            /** SIM application type USIM */
            static public final int USIM = 0;

            /** SIM application type ISIM */
            static public final int ISIM = 1;
        }

        /** Constants for SIM authentication types */
        public class SimAuth {
            private SimAuth() { }

            /** Authentication type for UICC challenge is EAP SIM. See RFC 4186 for details. */
            static public final int SIM = 0;

            /** Authentication type for UICC challenge is EAP AKA. See RFC 4187 for details. */
            static public final int AKA = 1;
        }
    }

    public class Configuration {
        private Configuration() { }

        /** Constants for configuration and provisioning capabilities */
        public class Capability {
            private Capability() { }

            /** UE Configuration Data Management Object */
            static public final int MO_UE_CONFIGURATION_DATA = 0;

            /** User Configuration Data Management Object */
            static public final int MO_USER_CONFIGURATION_DATA = 1;

            /** Group Configuration Data Management Object */
            static public final int MO_GROUP_CONFIGURATION_DATA = 2;

            /** Service Configuration Data Management Object */
            static public final int MO_SERVICE_CONFIGURATION_DATA = 3;
        }

        /** Constants for getErrorCode */
        public class Error {
            private Error() { }

            /** The configuration data is invalid (either malformed or
                too large for the storage target) */
            static public final int INVALID_DATA = 2;

            /** Failed to read/write the configuration data to storage */
            static public final int IO_FAILURE = 3;
        }

        /** Constants for configuration and provisioning notifications */
        public class Notification {
            private Notification() { }

            /**
             * Notification for configuration file update.
             *
             * <p><b>Parameters:</b>
             * <br>arg1 - configuration file storage
             * <br>arg2 - configuration file type
             * @see Constants.Configuration.Storage
             * @see Constants.Configuration.FileType
             */
            static public final int FILE_UPDATED = 1;
        }

        /** Constants for storage types */
        public class Storage {
            private Storage() { }

            /** Storage type is SIM which is default subscription */
            static public final int SIM_DEFAULT = 0;

            /** Storage type is SIM in SIM slot 1 */
            static public final int SIM_1 = 1;

            /** Storage type is SIM in SIM slot 2 */
            static public final int SIM_2 = 2;

            /** Storage type is ME (Mobile Equipment) */
            static public final int ME = 3;

            /** Use a storage decided by the ConfigurationService.
                This storage type can only be used for reads. */
            static public final int AUTO = 4;
        }

        /** Constants for MC configuration files */
        public class FileType {
            private FileType() { }

            /** PTT UE Configuration Management Object file */
            static public final int PTT_MO_UE = 0;

            /** PTT User Configuration Management Object file */
            static public final int PTT_MO_USER = 1;

            /** PTT Group Configuration Management Object file */
            static public final int PTT_MO_GROUP = 2;

            /** PTT Service Configuration Management Object file */
            static public final int PTT_MO_SERVICE = 3;
        }
    }
}

