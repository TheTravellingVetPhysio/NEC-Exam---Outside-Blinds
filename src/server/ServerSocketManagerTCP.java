package server;

import model.BlindsStatus;
import model.MessageType;
import shared.listener.BlindsUIListener;
import shared.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

public class ServerSocketManagerTCP
{
  private BlindsStatus status = BlindsStatus.DOWN;
  private Logger logger = Logger.getInstance();
  private PrintWriter out;
  private ServerSocket serverSocket;
  private final BlindsUIListener uiListener;

  public ServerSocketManagerTCP(int port, BlindsUIListener uiListener)
  {
    this.uiListener = uiListener;
    logger.log("Info", "Server TCP Blinds server on port: " + port);
    new Thread(() -> run(port)).start();
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
      logger.log("Error", "Failed to stop TCP server: " + e.getMessage());
    }
  }

  private void run(int port)
  {

    try (ServerSocket blindsSocket = new ServerSocket(port))
    {
      this.serverSocket = blindsSocket;
      while (true)
      {
        logger.log("Info", "Waiting for TCP command client...");
        Socket socket = blindsSocket.accept();

        new Thread(() -> handleClient(socket)).start();
      }
    }
    catch (IOException e)
    {
      if (serverSocket != null && !serverSocket.isClosed())
      {
        logger.log("Error", "TCP server was closed. ");
        throw new RuntimeException(e);
      }
    }
  }

  private void handleClient(Socket socket)
  {
    String clientAddress =
        socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

    logger.log("Info", "Connection established with client" + clientAddress);

    try (BufferedReader in = new BufferedReader(
        new InputStreamReader(socket.getInputStream()));)
    {
      out = new PrintWriter(socket.getOutputStream(), true);
      String request;

      while ((request = in.readLine()) != null)
      {
        logger.log("Info", "Client " + clientAddress + "> " + request);

        if (request.startsWith(MessageType.ACK.name()))
        {
          BlindsStatus confirmedStatus = BlindsStatus.valueOf(
              (request.split(":")[1]));
          if (uiListener != null)
            uiListener.onBlindsChanged(confirmedStatus);

          logger.log("Info", "Kvittering modtaget: " + request);
        }
        else if (request.startsWith(
            MessageType.COMMAND.name()))    // TCP clienten sender ikke commands, kun ACK, så denne del er ikke relevant
        {
          BlindsStatus reply = handleCommand(request.split(":")[1]);
          out.println(reply);
          logger.log("Info", "Server svarede: " + reply);
        }
      }
    }
    catch (IOException e)
    {
      logger.log("Error",
          "Connection established with client " + clientAddress);

      throw new RuntimeException(e);

    }

  }

  private synchronized BlindsStatus handleCommand(
      String request)   // Måske ikke relevant fordi TCP clienten ikke sender commands? Metoden kan slettes...
  {
    try
    {
      BlindsStatus command = BlindsStatus.valueOf(request);

      // Forretningslogik, der hører til i BlindsService jvf Single Responsibility...
      switch (command)
      {
        case UP ->
        {
          status = BlindsStatus.UP;
          return status;
        }

        case DOWN ->
        {
          status = BlindsStatus.DOWN;
          return status;
        }

      }
    }
    catch (IllegalArgumentException e)
    {
      logger.log("Error", "Failed to connect to client");
      throw new RuntimeException(e);
    }
    return status;
  }

  public void sendCommand(
      BlindsStatus status) // Håndterer sendCommand fra ServerSocketManagerUDP baseret på automatisk beregning på baggrund af sensordata
  {
    if (out == null)
    {
      logger.log("Error", "Blinds not connected..");
      return;
    }
    logger.log("Info", "Sending command: " + status.name());
    out.println(MessageType.COMMAND + ":" + status.name());
  }

}
