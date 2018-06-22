> *Refer to* [*README*](../README.md) *for main instruction file*

# Profiles

The profiles *XML* file contains a list of *NgnSipPreferences* elements under main root *<Profiles>* tag. It can be found in **android-ngn-stak/res/raw/profiles/** folder on the **MCOP SDK** project. Each *NgnSipPreferences* element contains the configuration of a MCPTT user.The configuration properties and their tags are described in the **Elements** section below. If a value isn't set, the application uses the default value stored in the code for that field.

After defining a new NgnSipPreferences, it needs to be referenced in the application, adding its name to the **clients HasMap**.

*e.g. for profile name:*

	<NgnSipPrefrences name="MyProfile_UserA">
	
*add the following line to clients HasMap:*

	clients.put("MyProfile_UserA", new String[]{"MyProfile_UserA","MyProfile_UserA","MyProfile_UserA"});

The client HashMap is defined in the **MainActivity.java** file for both **MCOP SDK** and **MCOP MCPTT Client**. To quickly find it in Android Studio, go to *Edit --> Find --> Find in path* and search for **clients = new HashMap<>()**, and add the *clients.put* line after it.

## demo.mcopenplatform.org

The [**demo.mcopenplatform.org**](https://demo.mcopenplatform.org/) platform can be used for testing purposes. 

Click on [**Request Access**](https://demo.mcopenplatform.org/reserve) and check the calendar there for an empty slot, and you'll receive credentials for five test users (with suffixes A to E).

The **Name** field (max. 10 characters) in the form will determine the name of the client ID. Check the **Example** section below for a specific example.

## ExampleExample **profiles** file with just one user configuration.

**For configuration from the [MCOP demo platform](https://demo.mcopenplatform.org/), replace "test" with the "Name" field submitted in the form.** The specific information is also provided by email after requesting a slot.	<Profiles>
   		<NgnSipPrefrences name="TEST_A">
            <DisplayName>TEST A</DisplayName>
            <IMPI>mcptt-test-A@organization.org</IMPI>
            <IMPU>sip:mcptt-test-A@organization.org</IMPU>
            <Password></Password>
            <IPVersion>ipv4</IPVersion>
            <Transport>UDP</Transport>
            <IPsecSecAgree>false</IPsecSecAgree>
            <McpttAnswerMode>true</McpttAnswerMode>
            <McpttGranted>true</McpttGranted>
            <McpttId>sip:mcptt_id_test_A@organization.org</McpttId>
            <McpttClientId>sip:mcptt_id_test_A@organization.org</McpttClientId>
            <McpttImplicit>true</McpttImplicit>
            <McpttIsEnableAffiliation>true</McpttIsEnableAffiliation>
            <McpttNameSpace>true</McpttNameSpace>
            <McpttPriority>7</McpttPriority>
            <McpttPrivAnswerMode>false</McpttPrivAnswerMode>
            <McpttPsiAffiliation>sip:mcptt-server-orig-part.organization.org</McpttPsiAffiliation>
            <McpttPsiCallGroup>sip:mcptt-server-orig-part.organization.org</McpttPsiCallGroup>
            <McpttPsiCallPreestablished>sip:mcptt-server-orig-part.organization.org</McpttPsiCallPreestablished>
            <McpttPsiCallPrivate>sip:mcptt-server-orig-part.organization.org</McpttPsiCallPrivate>
            <McpttPsiAuthentication>sip:mcptt-server-orig-part.organization.org</McpttPsiAuthentication>
            <Realm>organization.org</Realm>
            <PcscfPort>5070</PcscfPort>
            <PcscfHost>demo.mcopenplatform.org</PcscfHost>
        </NgnSipPrefrences>
	</Profiles>

**Note: In case of any typo or error in the profile resulting in not being able to parse it, the SDK will use its default values instead**. 

## Elements

Brief description and example of the configuration elements:* **DisplayName**: Display name.		e.g. Test A* **IMPI**: IP Multimedia Private Identity.		e.g. mcptt-test-A@organization.org* **IMPU**: IP Multimedia Public Identity.		e.g. sip:mcptt-test-A@organization.org* **Password**: Password.		e.g. password* **IPVersion**: IPV version.		e.g. ipv4

* **Transport**: Transport protocol.		e.g. UDP* **McpttAnswerMode**: "true" for auto answer mode, and "false" for manual answer mode.		e.g. true* **McpttGranted**: To insert GRANTED in the Invite message.		e.g. true* **McpttId**: MCPTT identifier.		e.g. sip:mcptt_id_test_A@organization.org* **McpttClientId**: MCPTT Client identifier.		e.g. sip:mcptt_id_test_A@organization.org* **McpttImplicit**: To insert IMPLICIT in the Invite message.		e.g. true* **McpttIsEnableAffiliation**: To enable MCPTT affiliation features.		e.g. true* **McpttPriority**: Priority level for Private or Group MCPTT calls. From 1 (lowest), to 15 (highest).		e.g. 7* **McpttPrivAnswerMode**: "true" for automatic answer mode, and "false" for manual answer mode.		e.g. false* **McpttPsiAffiliation**: Public Service Identity for Affiliations.		e.g. sip:mcptt-server-orig-part.organization.org* **McpttPsiCallGroup**: Public Service Identity for Group Calls.		e.g. sip:mcptt-server-orig-part.organization.org* **McpttPsiCallPreestablished**: Public Service Identity for Preestablished Calls.		e.g. sip:mcptt-server-orig-part.organization.org* **McpttPsiCallPrivate**: Public Service Identity for Private calls.		e.g. sip:mcptt-server-orig-part.organization.org		* **McpttPsiAuthentication**: Public Service Identity for Authentication.		e.g. sip:mcptt-server-orig-part.organization.org* **Realm**: domain.		e.g. organization.org* **McpttEnableMbms**: To enable MBMS (Multimedia Broadcast Multicast Services).		e.g. true* **PcscfHost**: Host name or ip address of the P-CSCF (Proxy-Call Session Control Function).		e.g. 192.168.16.193* **PcscfPort**: Port number of the P-CSCF.		e.g. 5060
	
