package client;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocketManagerTCP
{
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private final String host;
  private final int port;

  public ClientSocketManagerTCP(String host, int port)
  {
    this.host = host;
    this.port = port;
  }

  


}
