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
      }
    }
    catch (IOException e)
    {
      logger.log("Error",
          "Connection established with client " + clientAddress);

      throw new RuntimeException(e);
    }
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
