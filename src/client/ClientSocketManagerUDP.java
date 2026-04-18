package client;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientSocketManagerUDP
{
  private DatagramSocket socket;
  private int port;
  private InetAddress IPAddress;

  private final int DATAGRAM_SIZE = 20;

  public ClientSocketManagerUDP(String host, int port)
  {
    connect(host, port);
  }

  public void connect(String host, int port)
  {
    // Disconnect from existing connection (if exist)
    if (socket != null)
      disconnect();

    try
    {
      // Create client socket
      socket = new DatagramSocket();

      // Translate the hostname to IP using DNS
      IPAddress = InetAddress.getByName(host);
      this.port = port;

      System.out.println("Client setup address and port to server.");
    }
    catch (IOException e)
    {
      System.out.println("Error: Client failed to setup address and port to server.");
    }
  }
  public void disconnect()
  {
    socket.close(); // In UDP, closing the socket is not informed to the server
    System.out.println("Client closed connection with server.");
  }

}
