// IMBMSGroupComListener.aidl
package org.mcopenplatform.iapi;

interface IMBMSGroupComListener {

	/* Received regularly */
	void notifySAIList(in int[] SAI);

	/* Received regularly */
	void notifyCellInfo(int MCC, int MNC, int ECI);
	
	/* Received when the group comm is monitored */
	void notifyMBMSGroupCommAvailability(long TMGI, int available, int goodQuality);
	
	/* Received after a request for opening a group comm */
	void notifyOpenMBMSGroupCommResult(long TMGI, int result, String netInterfaceName);
	
	/* Received after a request for closing a group comm */
	void notifyCloseMBMSGroupCommResult(long TMGI, int result);

}