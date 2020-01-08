/////////////////////////////////////////////////////////////
//
// Put your information here
//
/////////////////////////////////////////////////////////////
import java.net.*;
import java.io.*;
import java.util.*;

// This is mostly a copy of the sample code.  Use what you want, delete the rest.
// Note: This code is not well organized. It should be broken into smaller methods.

public class MyStaticWebServer {

    // You can use this to map extensions to MIME types.  Feel free to add other mappings as desired.
    private static final HashMap<String, String> extensions = new HashMap<String, String>();
    static {
        extensions.put("jpeg", "image/jpeg");
        extensions.put("jpg", "image/jpeg");
        extensions.put("png", "image/png");
        extensions.put("gif", "image/gif");
        extensions.put("html", "text/html");
        extensions.put("htm", "text/html");
        extensions.put("pdf", "application/pdf");
        extensions.put("ico", "image/vnd.microsoft.icon");
    }

    // This method demonstrates how to isolate a file's extension
    public static String getExtension(String filename) {
        String type = "text/plain";
        int i = filename.lastIndexOf('.');
        if (i > 0) {
            String extension = filename.substring(i + 1);
            if (extensions.containsKey(extension)) {
                type = extensions.get(extension);
            }
        }
        return type;
    }


  public static void send404(PrintStream out, String toPrint) {
    out.println("HTTP/1.1 404 Not Found");
    out.println("Content-Type: text/html");
    out.println("Content-Length: " + toPrint.length());
    out.println("Connection: close");
    out.println("");
    out.println(toPrint);
  }

  public static void main(String[] args) throws IOException {

    // Create a socket that listens on port 8534.
    int port = 8534;
    ServerSocket serverSocket = new ServerSocket(port);

    // Handle multiple requests sequentially
    while (true) {
      System.out.println("\n\nAwaiting new connection on port " + port);

      // Return a Socket object for the next connection in the queue
      Socket socket = serverSocket.accept();

      // Created a BufferedReader that can read from the socket
      BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Create a PrintStream than can write to the socket
      // Passing "true" as the second parameter causes each write to be followed by a
      // flush.
      PrintStream out = new PrintStream(socket.getOutputStream(), true);

      // Read the main command.
      String command = input.readLine();
      System.out.println("Command Received: =>" + command + "<=");

      // Read the request headers
      System.out.println("\nRequest Headers:");
      String headerLine = input.readLine();
      while (headerLine != null && !headerLine.isEmpty()) {
        System.out.println("\t" + headerLine);
        headerLine = input.readLine();
      }

      // split the command by spaces.
      String[] parts = command.split("\\s+");
      System.out.printf("Command; %s; path %s; protocol %s\n", parts[0], parts[1], parts[2]);

      String filename = parts[1];

      // If the path begins with "/", remove the "/".
      if (filename.startsWith("/")) {
        filename = filename.substring(1);
      }

      File f = new File(filename);

      // send 404 if file doesn't exist, or is not readable.
      if (!f.exists() || !f.canRead() || !f.isFile()) {
        System.out.println(filename + " not found.  Returning 404.");
        String toPrint = "<html><body>Problem finding/reading \"" + filename + "\"</body></html>";
        send404(out, toPrint);
        socket.close();
        continue;
      }

      FileInputStream fis;

      try {
        fis = new FileInputStream(f);
      } catch (Exception e) {
        String toPrint = "<html><body>Problem opening/reading \"" + filename + "\"</body></html>";
        send404(out, toPrint);
        socket.close();
        break;
      }

      // Respond
      out.println("HTTP/1.1 200 OK");
      out.println("Content-Type: text/html");
      out.println("Content-Length: " + f.length());
      out.println("Connection: close");
      out.println("");

      // read data from the file and send it to the client.
      byte[] buffer = new byte[8192];
      int read = fis.read(buffer);
      while (read != -1) {
        out.write(buffer, 0, read);
        read = fis.read(buffer);
      }
      fis.close();

      socket.close();

    } // end while(true)

    serverSocket.close();

    // When the connection ends, so does this program.
  }



}