> *Refer to* [*README*](../README.md) *for main instruction file*

# MCOP MOCK

The MCOP Mock app is used to bypass the SIM authentication in order to test the **MCOP SDK** in those devices that don't have a proper SIM plugin due to privilege constraints.

It uses **AKAv2** (*Authentication and Key Agreement*, [*RFC 3310*](https://tools.ietf.org/html/rfc3310)) for authentication. AKA is a challenge-response based mechanism that uses symmetric cryptography.

## Parameters

* **IMPI**: The IP Multimedia Private Identity used to authenticate.

		e.g. mcptt-test-A@organization.org

* **DOMAIN**: domain.

		e.g. organization.org

* **PASSWORD**: A shared secret key for the authentication process.

		e.g. 0123456789

## Usage

Open the **Mock app** and enter the IMPI, DOMAIN and PASSWORD values. After that, it can be even closed.

The **MCOP SDK** or **MCOP MCPTT** Client need to be opened after the data has been entered in the Mock App.

## Installation

Download the **Mock apk** from [here](https://demo.mcopenplatform.org/gitlist/mcop/MCOP-SDK.git/raw/master/mock/mockAPP.apk).

It can be installed directly from an Android device, or using [Android ADB](https://developer.android.com/studio/command-line/adb) from a computer.

## Screenshot
		
![MOCK](../images/images_mockAuth.png)

## Response

The Mock app provides a response according to the standard specified in [ETSI TS 131 102](http://www.etsi.org/deliver/etsi_ts/131100_131199/131102/14.04.00_60/ts_131102v140400p.pdf) on section *7.1.2.1 GSM/3G security context*, in base64 format.

## Disclaimer

The Mock App internally uses **amf = 0x0000 (hex)** and **OP = 0x00000000000000000000000000000000 (hex)**.




