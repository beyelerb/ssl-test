/**
 * Copyright (c) Codice Foundation
 *
 * <p>This is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or any later version.
 *
 * <p>This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public
 * License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */

import java.io.*;
import java.security.*;
import javax.net.ssl.*;

public class SSLServer {
  public static void main(String[] args) {
    int port = 443;
    boolean clientAuth = false;

    if ((args.length == 0) || (args[0].equalsIgnoreCase("-h"))) {
      printUsage();
      System.exit(0);
    }

    try {
      port = Integer.parseInt(args[0]);
      if (args.length > 1) {
        clientAuth = args[1].equalsIgnoreCase("-clientauth");
      }
    } catch (Exception e) {
      printUsage();
      System.exit(1);
    }

    try {
      // Prepare the keystore usage - assumed that the env variables specify keystore/trustore
      String keystore = System.getProperties().getProperty("javax.net.ssl.keyStore");
      String password = System.getProperties().getProperty("javax.net.ssl.keyStorePassword");
      KeyStore ks = KeyStore.getInstance("JKS");
      System.out.println("Loading keystore from: " + keystore);
      InputStream ksIs = new FileInputStream(keystore);
      try {
        ks.load(ksIs, password.toCharArray());
      } finally {
        ksIs.close();
      }
      KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory
          .getDefaultAlgorithm());
      kmf.init(ks, password.toCharArray());

      // set up the SSL context
      SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
      sslContext.init(kmf.getKeyManagers(), null, null);
      SSLServerSocketFactory ssf = sslContext.getServerSocketFactory();
      SSLServerSocket serverSocket
          = (SSLServerSocket) ssf.createServerSocket(port);
      if (clientAuth) {
        System.out.println("Requiring client authentication...");
        serverSocket.setNeedClientAuth(true);
      }

      // start the server
      printServerInformation(serverSocket);
      SSLSocket socket = (SSLSocket) serverSocket.accept();

      // connected - if client authenticated, print out DN
      if (clientAuth) {
        try {
          System.out.println("Client connected: " + socket.getSession().getPeerCertificateChain()[0].getSubjectDN());
        } catch (Exception e) {
        }
      }

      // read input and send a simple output
      BufferedWriter writer = new BufferedWriter(
          new OutputStreamWriter(socket.getOutputStream()));
      InputStreamReader reader = new InputStreamReader(socket.getInputStream());

      // read a single character and send a simple response
      reader.read();
      writer.write("HTTP/1.0 200 OK\nContent-Type: text/html\n\n<html><body>connected</body></html>\n");
      writer.flush();
      writer.close();
      reader.close();
      socket.close();

      System.out.println("Connection successfully established - terminating.");
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static void printUsage() {
    System.out.println("Usage:  java " + SSLServer.class.getName() + " <port> [-clientAuth]\n" +
        "  port         the port number that the server should listen on\n" +
        "  -h           prints usage information\n" +
        "  -clientAuth  if included sets the server to request mutual authentication\n\n" +
        "To test specific keystores and truststores, they should be provided via the env variables:\n" +
        "  -Djavax.net.ssl.keyStore=<keystorefile>\n" +
        "  -Djavax.net.ssl.keyStorePassword=<password>\n" +
        "  -Djavax.net.ssl.trustStore=<truststore file>\n" +
        "  -Djavax.net.ssl.trustStorePassword=<password>\n\n" +
        "For additional debug information, add the ssl debug option:\n" +
        "  -Djavax.net.debug=ssl,handshake\n");
  }

  private static void printServerInformation(SSLServerSocket socket) {
    System.out.println("Server listening on port " + socket.getLocalPort());
    System.out.println("  client authentication needed: " + socket.getNeedClientAuth());
  }
}