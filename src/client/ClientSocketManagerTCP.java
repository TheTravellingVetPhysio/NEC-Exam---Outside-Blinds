package client;

import model.BlindsStatus;
import shared.listener.BlindsUIListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocketManagerTCP implements BlindsClient
{
  private BlindsUIListener listener;
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private final String host;
  private final int port;

  public ClientSocketManagerTCP(String host, int port)
  {
    this.host = host;
    this.port = port;
    connect(host, port);
  }

  public void addListener(BlindsUIListener listener)
  {
    this.listener = listener;
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
      System.out.println("Error: Client failed to establish connection to server.");
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

      in     = null;
      out    = null;
      socket = null;
    }
    catch (IOException e)
    {
      System.out.println("Error: Client failed to close the connection to server.");
    }
  }

  @Override public void send(BlindsStatus status)
  {
    out.println(status.name());

  }

  @Override public void receiveCommand()
  {
    new Thread(() -> {
      try
      {
        String command;
        while ((command = in.readLine()) != null)
        {
          switch (command)
          {
            case "OPEN" ->
            {
              // fysisk åbn persiennen
              if (listener != null)
                listener.onBlindsChanged(BlindsStatus.OPEN); //  notificerer UI
              send(BlindsStatus.OPEN);   // kvitter tilbage til server
            }
            case "CLOSE" ->
            {
              // fysisk luk persiennen
              if (listener != null)
                listener.onBlindsChanged(BlindsStatus.CLOSED); // notificerer UI
              send(BlindsStatus.CLOSED); // kvitter tilbage til server
            }
          }
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }).

          start();
  }
}
