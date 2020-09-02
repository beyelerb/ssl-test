<!--
/*
 * Copyright (c) Codice Foundation
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or any later version. 
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
-->

# SSL Testing Utilities

SSL Testing Utilities consist of simple java client and servers that can be used to test out ssl connections.
They provide minimal implementations, i.e. no dependencies outside java itself, of an ssl test client and an ssl server.
The intent is to provide simple utilities that can be typed in and compiled on-site - even if there is no internet
available.

Debugging SSL connections can be difficult on live systems. To turn on ssl debugging for java means to be inundated
with huge quantities of output - most of which may not relate to the actual connection being debugged. These utilities
allow testing of SSL connections using the same keystore and truststore files that actual clients/servers use but
with only a single connection attempt. This makes it easy to isolate and debug SSL connection problems due to 
java keystore and truststore issues.

## SSLClient
SSLClient attempts to make a single connection to the specified server and port. SSLClient can validate keystore
and truststore files by specifying them via environment variables. If there are issues with trusting the server
certificate, or if the server can't build a chain of trust for the client certificate, these will be obvious.

SSLClient attempts to connect to the specified server, sends a single byte to elicit a response, and then exits.
If it is successful, it prints out a simple message stating that and exits. If there is an exception thrown, it
prints the exception and aborts.

The following environment variables can be set to control what context the client uses:
* -Djavax.net.ssl.keyStore=\<location of keystore file\>
* -Djavax.net.ssl.keyStorePassword=\<password of keystore file\>
* -Djavax.net.ssl.trustStore=\<location of truststore file\>
* -Djavax.net.ssl.trustStorePassword=\<password of truststore file\>

For complete debugging of the SSL handshake, the following java parameter can be added:
* -Djavax.net.debug=ssl,handshake

To get usage information - include the `-h` option:

```
$ java SSLClient -h
Usage: SSLClient <host> <port>
  -h  prints usage information
Make sure the following java options are set to the keystores/truststores you're testing:

  -Djavax.net.ssl.keyStore=<location of your keystore>
  -Djavax.net.ssl.keyStorePassword=<keystore password>
  -Djavax.net.ssl.trustStore=<location of your truststore>
  -Djavax.net.ssl.trustStorePassword=<truststore password>

For handshaking debugging output, add the following option:

  -Djavax.net.debug=ssl,handshake
```

## SSLServer
SSLServer allows you to start up a server using a specific keystore and truststore. It also allows you to 
specify whether you want mutual authentication enabled \(required\). The server listens for a connection
on the port specified, receives a single character and then sends a very simple HTML response before exiting.

Similar to the SSClient, the following environment variables can be set to specify the keystore and truststore
that the server uses for its identity and its trust:
* -Djavax.net.ssl.keyStore=\<location of keystore file\>
* -Djavax.net.ssl.keyStorePassword=\<password of keystore file\>
* -Djavax.net.ssl.trustStore=\<location of truststore file\>
* -Djavax.net.ssl.trustStorePassword=\<password of truststore file\>

Likewise, for complete debugging of the SSL handshake, the following java parameter can be added:
* -Djavax.net.debug=ssl,handshake

SSLServer provides an option to force client authentication \(mutual TLS\). This option is specified using
the `-clientAuth` parameter.

To get usage information - include the `-h` option:

```
$ java SSLServer -h
Usage:  java SSLServer <port> [-clientAuth]
  port         the port number that the server should listen on
  -h           prints usage information
  -clientAuth  if included sets the server to request mutual authentication

To test specific keystores and truststores, they should be provided via the env variables:
  -Djavax.net.ssl.keyStore=<keystorefile>
  -Djavax.net.ssl.keyStorePassword=<password>
  -Djavax.net.ssl.trustStore=<truststore file>
  -Djavax.net.ssl.trustStorePassword=<password>

For additional debug information, add the ssl debug option:
  -Djavax.net.debug=ssl,handshake
```
## Compiling
### What you need ###
These were designed to be compiled directly rather than built with some sort of build tool such as maven, gradle, etc.
To compile these files, only the Java SDK needs to be installed \(specifically javac\).

* [Install J2SE 8 SDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
* Make sure that your JAVA\_HOME environment variable is set to the newly installed JDK location, and that your PATH includes %JAVA\_HOME%\bin (Windows) or $JAVA\_HOME$/bin (\*NIX).


### How to compile ###
Compiling the files is relatively easy. Copy/paste the files from the raw view here, or clone the repository to pull
all the files at once.

```
git clone git://github.com/beyelerb/ssltest.git
```
Change to the root directory of the cloned ssltest repository. Run the following command:

```
javac *.java
```

This will compile each java file and leave the .class file in the current directory. Each class file can then
 be run via the java command.


## How to Run
The java runtime executes files on the `classpath` - it searches all class files on the `classpath` until it finds
the one specified. In this case, probably the easiest thing to do is to specify the classpath to use as part of the
java command line using the `-cp` argument. Since these files do not have a package name, you only need to specify
the directory where the .class files are located.

* identify the directory where the .class files are located
* run the `java` command with the `-cp` argument specifying the directory above

The following example illustrates how to compile and run the SSLClient from the current directory - it assumes
that the SSLClient.java file exists in the current directory:

```
$ javac *.java
$ java -cp . SSLClient <server hostname> <port>
```

If you will be running and specifying the keystore and truststore environment variables multiple times, it is
probably easiest to export an environment variable with all of the settings and then reference that at runtime:

```shell script
$ export JAVA_OPTS="-Djavax.net.ssl.keyStore=keystore.jks -Djavax.net.ssl.keyStorePassword=changeit -Djavax.net.ssl.trustStore=truststore.jks -Djavax.net.ssl.trustStorePassword=changeit"
$ java -cp . ${JAVA_OPTS} SSLClient <server hostname> <port>
```

The SSLServer is run using the same steps as the SSLClient. For example to run the server using the same
keystore and truststore as above, listening on port 8080, and requiring mutual SSL authentication:

```
$ java -cp . ${JAVA_OPTS} SSLServer localhost 8080 -clientAuth
```

## Copyright / License
Copyright (c) Codice Foundation
 
This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License 
as published by the Free Software Foundation, either version 3 of the License, or any later version. 
 
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
See the GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public License is distributed along with this program and can be found at
<http://www.gnu.org/licenses/lgpl.html>.
 