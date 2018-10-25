# tsapi.pro - Telephony Services API Properties File for JTAPI
#
# This file must be located in one of the directories found in CLASSPATH.

# ---------------------------------------------------------------------------
# This is a list of the servers offering Telephony Services via TCP/IP.
# Either domain name or IP address may be used; the default port number is 450.
# The form is: host_name=port_number   For example:
#
# tserver.mydomain.com=450
# 127.0.0.1=450
#10.10.0.32=450
10.13.18.20
# (Remove the '#' when creating actual server entries.)

# ---------------------------------------------------------------------------
# When accessing Telephony Services via a secure, encrypted connection, the
# Application Enablement (AE) Services server sends its certificate to the
# JTAPI client, and the JTAPI client verifies that the certificate is signed
# by a trusted Certificate Authority (CA).
#
# If your organization has installed its own certificate on the AE Server,
# then the JTAPI client must have access to the trusted CA certificate(s)
# for the AE Services server certificate.  Provide the location of a Java
# Key Store containing the trusted CA certificate(s) and the password for
# the Java Key Store here.  For example:
#
#trustStoreLocation=C:\\Avaya\\jtapi-sdk-6.3.3\\resources\\CA.jks
#trustStorePassword=password
#
# The JTAPI client can also be configured with a client key and certificate,
# which will be used to identify the JTAPI client to the AE Services server.
# The JTAPI client will send the client certificate only if the AE Services
# server requests it while setting up of the encrypted connection.
#
# To use client certificates, provide the location of a Java key store containing
# the private key and the client certificate, and the password for the Java key 
# store.
#
#keyStoreLocation=C:\\Avaya\\jtapi-sdk-6.3.3\\resources\\Client.jks
#keyStorePassword=password
# 
#
# (Note the use of "\\" instead of "\" when specifying Windows file locations.)

# ---------------------------------------------------------------------------
# This file may specify alternate TSAPI links (Tlinks) for a preferred Tlink.
# The format of each entry is:
#
# Alternates(preferred_Tlink)=alternate_Tlink_1:alternate_Tlink_2:...
#
# Each entry may specify between one and four alternate Tlinks for the
# preferred Tlink.  Up to 16 entries are allowed.
#
# For example:
#
# Alternates(AVAYA#CM1#CSTA#AES1)=AVAYA#CM1#CSTA#AES2:AVAYA#CM1#CSTA#AES3
#

#[Telephony Servers]
10.13.18.20
#10.10.0.32=450

