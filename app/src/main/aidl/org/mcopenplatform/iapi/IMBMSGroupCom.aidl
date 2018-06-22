// IMBMSGroupCom.aidl
package org.mcopenplatform.iapi;

import org.mcopenplatform.iapi.IMBMSGroupComListener;

interface IMBMSGroupCom {
    void registerApplication(IMBMSGroupComListener listener);

    void startMBMSGroupCommMonitoring(long TMGI, in int[] sai, in int[] frequencies, int QCI);
    void stopMBMSGroupCommMonitoring(long TMGI);
	
    void openGroupComm(long TMGI, in int[] sai, in int[] frequencies);
    void closeGroupComm(long TMGI);
}