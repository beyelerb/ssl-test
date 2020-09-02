import java.io.InputStream;
import java.io.OutputStream;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class SSLClient {
  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("Usage: " + SSLClient.class.getName() + " <host> <port>");
      System.out.println("Make sure the following java options are set to the keystores/truststores you're testing:\n");
      System.out.println("  -Djavax.net.ssl.keyStore=<location of your keystore>\n" +
                         "  -Djavax.net.ssl.keyStorePassword=<keystore password>\n" +
                         "  -Djavax.net.ssl.trustStore=<location of your truststore>\n" +
                         "  -Djavax.net.ssl.trustStorePassword=<truststore password>\n");
      System.out.println("For handshaking debugging output, add the following option:\n");
      System.out.println("  -Djavax.net.debug=ssl,handshake\n");
      System.exit(1);
    }
    try {
      SSLSocketFactory sslsocketfactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
      SSLSocket sslsocket = (SSLSocket) sslsocketfactory.createSocket(args[0], Integer.parseInt(args[1]));

      InputStream in = sslsocket.getInputStream();
      OutputStream out = sslsocket.getOutputStream();

      // Write a test byte to get a reaction :)
      out.write(1);

      while (in.available() > 0) {
        System.out.print(in.read());
      }
      System.out.println("Successfully connected");

    } catch (Exception exception) {
      exception.printStackTrace();
    }
  }
}
