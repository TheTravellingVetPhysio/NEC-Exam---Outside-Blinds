package server;

import model.BlindsStatus;
import model.MessageType;
import shared.listener.BlindsServiceUIListener;
import shared.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerSocketManagerTCP
{
  private BlindsStatus status = BlindsStatus.DOWN;
  private final Logger logger = Logger.getInstance();
  private final List<PrintWriter> clients = new CopyOnWriteArrayList<>();
  private ServerSocket serverSocket;
  private final BlindsServiceUIListener uiListener;

  public ServerSocketManagerTCP(int port, BlindsServiceUIListener uiListener)
  {
    this.uiListener = uiListener;
    logger.log("INFO", "Server TCP Blinds server on port: " + port);
    new Thread(() -> run(port), "TCP-Server").start();
  }

  public ServerSocketManagerTCP(int port)
  {
    this(port, null);
  }

  public void stop()
  {
    try
    {
      if (serverSocket != null && !serverSocket.isClosed())
        serverSocket.close();
    }
    catch (IOException e)
    {
      logger.log("ERROR", "Failed to stop TCP server: " + e.getMessage());
    }
  }

  private void run(int port)
  {
    try (ServerSocket blindsSocket = new ServerSocket(port))
    {
      this.serverSocket = blindsSocket;

      while (!blindsSocket.isClosed())
      {
        logger.log("INFO", "Waiting for TCP command client...");
        Socket socket = blindsSocket.accept();

        new Thread(() -> handleClient(socket), "TCP-Client-Handler").start();
      }
    }
    catch (IOException e)
    {
      logger.log("INFO", "TCP server stopped.");
    }
  }

  private void handleClient(Socket socket)
  {
    String clientAddress =
        socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

    logger.log("INFO", "Connection established with client " + clientAddress);

    PrintWriter clientOut = null;

    try (
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()))
    )
    {
      clientOut = new PrintWriter(socket.getOutputStream(), true);
      clients.add(clientOut);

      String request;

      while ((request = in.readLine()) != null)
      {
        logger.log("INFO", "Client " + clientAddress + "> " + request);

        if (request.startsWith(MessageType.ACK.name()))
        {
          String[] parts = request.split(":");

          if (parts.length == 2)
          {
            BlindsStatus confirmedStatus = BlindsStatus.valueOf(parts[1]);
            status = confirmedStatus;

            if (uiListener != null)
              uiListener.onBlindsChanged(confirmedStatus);

            logger.log("INFO", "ACK received: " + request);
          }
        }
      }
    }
    catch (IOException e)
    {
      logger.log("ERROR", "Connection lost with client " + clientAddress);
    }
    finally
    {
      if (clientOut != null)
        clients.remove(clientOut);

      try
      {
        socket.close();
      }
      catch (IOException ignored)
      {
      }
    }
  }

  // Sender en TCP-kommando til alle forbundne blinds-klienter.
  // Kaldes af BlindsStateListenerService når blinds ændrer tilstand.
  public void sendCommand(BlindsStatus status)
  {
    if (clients.isEmpty())
    {
      logger.log("ERROR", "No blinds clients connected.");
      return;
    }

    this.status = status;
    String message = MessageType.COMMAND + ":" + status.name();

    logger.log("INFO", "Sending command: " + message);

    for (PrintWriter client : clients)
    {
      client.println(message);
    }
  }

  public BlindsStatus getStatus()
  {
    return status;
  }
}