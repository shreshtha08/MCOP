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

import android.os.Messenger;
import android.os.Parcelable;
import android.os.Parcel;
import android.os.Handler;

public class McopMessenger implements Parcelable {
    private Messenger m;

    public void writeToParcel(Parcel out, int flags) {
        m.writeToParcel(out, flags);
    }

    public int describeContents() { return 0; }

    public static final Parcelable.Creator<McopMessenger> CREATOR
        = new Parcelable.Creator<McopMessenger>() {
        public McopMessenger createFromParcel(Parcel in) {
            return new McopMessenger(in);
        }

        public McopMessenger[] newArray(int size) {
            return new McopMessenger[size];
        }
    };

    public McopMessenger(Handler h) {
        this.m = new Messenger(h);
    }

    private McopMessenger(Parcel p) {
        this.m = Messenger.CREATOR.createFromParcel(p);
    }

    public Messenger getMessenger() { return m; }
}

