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

package org.mcopenplatform.muoapi.datatype;


public class Client {
    private ClientSIM clientSIM;

    public ClientSIM getClientSIM() {
        return clientSIM;
    }

    public void setClientSIM(ClientSIM clientSIM) {
        this.clientSIM = clientSIM;
    }

    public class ClientSIM{
        private String impu;
        private String impi;
        private String domain;
        private String pcscf;
        private int pcscfPort;
        private String imsi;
        private String imei;

        public String getImpu() {
            return impu;
        }

        public void setImpu(String impu) {
            this.impu = impu;
        }

        public String getImpi() {
            return impi;
        }

        public void setImpi(String impi) {
            this.impi = impi;
        }

        public String getDomain() {
            return domain;
        }

        public void setDomain(String domain) {
            this.domain = domain;
        }

        public String getPcscf() {
            return pcscf;
        }

        public void setPcscf(String pcscf) {
            this.pcscf = pcscf;
        }

        public int getPcscfPort() {
            return pcscfPort;
        }

        public void setPcscfPort(int pcscfPort) {
            this.pcscfPort = pcscfPort;
        }

        public String getImsi() {
            return imsi;
        }

        public void setImsi(String imsi) {
            this.imsi = imsi;
        }

        public String getImei() {
            return imei;
        }

        public void setImei(String imei) {
            this.imei = imei;
        }
    }
}