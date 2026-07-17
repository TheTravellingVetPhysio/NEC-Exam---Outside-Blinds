package client;

import model.BlindsStatus;
import model.MessageType;
import shared.listener.BlindsServiceUIListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientSocketManagerTCP implements BlindsClient
{
  private BlindsServiceUIListener listener;
  private Socket socket;
  private BufferedReader in;
  private PrintWriter out;
  private final String host;
  private final int port;
  private final String clientName;

  public ClientSocketManagerTCP(String host, int port, String clientName)
  {
    this.host       = host;
    this.port       = port;
    this.clientName = clientName;
    connect(host, port);
  }

  public void addListener(BlindsServiceUIListener listener)
  {
    this.listener = listener;
  }

  @Override public void connect(String host, int port)
  {
    if (socket != null && !socket.isClosed())
      disconnect();

    try
    {
      socket = new Socket(host, port);

      in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      out = new PrintWriter(socket.getOutputStream(), true);
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
    System.out.println("[" + clientName + "] Sending ACK: " + status.name());

    out.println(MessageType.ACK + ":" + status.name());
  }

  @Override public void receiveCommand()
  {
    new Thread(() -> {
      try
      {
        String command;
        while ((command = in.readLine()) != null)
        {
          System.out.println("[" + clientName + "] Received: " + command);

          if (command.startsWith(MessageType.COMMAND.name()))
          {
            String action = command.split(":")[1]; // "UP" eller "DOWN"
            switch (action)
            {
              case "UP" ->
              {
                if (listener != null)
                  listener.onBlindsChanged(BlindsStatus.UP);

                send(BlindsStatus.UP);
              }

              case "DOWN" ->
              {
                if (listener != null)
                  listener.onBlindsChanged(BlindsStatus.DOWN);

                send(BlindsStatus.DOWN);
              }
            }
          }
        }
      }
      catch (IOException e)
      {
        e.printStackTrace();
      }
    }).start();
  }
}
