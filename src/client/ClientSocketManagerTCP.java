package client;

import model.BlindsStatus;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.Socket;

public class ClientSocketManagerTCP implements BlindsClient
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

  @Override public void connect(String host, int port)
  {
    // Disconnect from existing connection (if exist)
    if (socket != null && !socket.isClosed())
      disconnect();

    try
    {
      // Create client socket, connect to server
      socket = new Socket(host, port);

      // Create input stream attached to the socket
      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      // Create output stream attached to the socket
      out = new PrintWriter(socket.getOutputStream(), true);
      //      System.out.println("Client established connection with server.");
    }
    catch (IOException e)
    {
      System.out.println(
          "Error: Client failed to establish connection to server.");
    }
  }

  @Override public void disconnect()
  {
    try
    {
      if (in != null)
        in.close();
      if (out != null)
        out.close();
      if (socket != null && !socket.isClosed())
        socket.close();
      //      System.out.println("Client closed connection with server.");

      in = null;
      out = null;
      socket = null;
    }
    catch (IOException e)
    {
      System.out.println(
          "Error: Client failed to close the connection to server.");
    }
  }

  @Override public void send(BlindsStatus status)
  {
    String message = status.name();
    byte[] sendData = message.getBytes();

    DatagramPacket packet = new DatagramPacket(sendData, sendData.length);

  }

  @Override public void receiveCommand()
  {
    try {
      BufferedReader in = new BufferedReader(
          new InputStreamReader(socket.getInputStream())
      );

      String command;
      while ((command = in.readLine()) != null) {
        switch (command) {
          case "OPEN"   -> {
            // fysisk åbn persiennen
            send(BlindsStatus.OPEN);   // kvitter tilbage til server
          }
          case "CLOSE" -> {
            // fysisk luk persiennen
            send(BlindsStatus.CLOSED); // kvitter tilbage til server
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
