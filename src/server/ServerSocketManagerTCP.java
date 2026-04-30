package server;

import model.BlindsStatus;
import shared.logger.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManagerTCP
{
  private BlindsStatus status = BlindsStatus.CLOSED;
  private Logger logger = Logger.getInstance();
  private PrintWriter out;

  public ServerSocketManagerTCP(int port)
  {
    logger.log("Info", "Server TCP Blinds server on port: " + port);
    new Thread(() -> run(port)).start();
  }

  private void run(int port)
  {

    try (ServerSocket blindsSocket = new ServerSocket(port))
    {
      while (true)
      {
        logger.log("Info", "Waiting for TCP command client...");
        Socket socket = blindsSocket.accept();

        new Thread(() -> handleClient(socket)).start();
      }
    }
    catch (IOException e)
    {
      logger.log("Error", "TCP server was closed. ");
      throw new RuntimeException(e);
    }

  }

  private void handleClient(Socket socket)
  {
    String clientAddress = socket.getInetAddress().getHostAddress() + ":" + socket.getPort();

    logger.log("Info", "Connection established with client" + clientAddress);

    try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));)
    {
      out = new PrintWriter(socket.getOutputStream(), true);
      String request;

      while ((request = in.readLine()) != null)
      {
        logger.log("Info", "Client " + clientAddress + "> " + request);

        BlindsStatus reply = handleCommand(request);
        out.println(reply);
        logger.log("Info", "Server Replied " + reply);
      }
    }
    catch (IOException e)
    {
      logger.log("Error", "Connection established with client " + clientAddress);

      throw new RuntimeException(e);

    }

  }

  private synchronized BlindsStatus handleCommand(String request)
  {
    try
    {
      BlindsStatus command = BlindsStatus.valueOf(request);

      switch (command)
      {
        case OPEN ->
        {
          status = BlindsStatus.OPEN;
          return status;
        }

        case CLOSED ->
        {
          status = BlindsStatus.CLOSED;
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
    out.println(status.name());
  }

}
