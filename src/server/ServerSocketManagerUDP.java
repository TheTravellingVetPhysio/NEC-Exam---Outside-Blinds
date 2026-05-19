package server;

import model.SensorType;
import service.BlindsService;
import shared.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class ServerSocketManagerUDP
{
  private final BlindsService blindsService;
  private final ServerSocketManagerTCP serverSocketManagerTCP;
  private final int port;
  private DatagramSocket serverSocket;

  private final int DATAGRAM_SIZE = 32;

  public ServerSocketManagerUDP(int port, BlindsService blindsService,
      ServerSocketManagerTCP serverSocketManagerTCP)
  {
    this.port = port;
    this.blindsService = blindsService;
    this.serverSocketManagerTCP = serverSocketManagerTCP;

    new Thread(this::run).start();
  }

  public void stop()
  {
    if (serverSocket != null && !serverSocket.isClosed())
      serverSocket.close();
  }

  public void run()
  {
    System.out.println("Starting server...");
    try
    {
      serverSocket = new DatagramSocket(port);

      while (true)
      {
        byte[] receiveData = new byte[DATAGRAM_SIZE];
        DatagramPacket receivePacket = new DatagramPacket(receiveData,
            receiveData.length);
        System.out.println("Waiting for a new value from sensor.");
        serverSocket.receive(receivePacket);

        String request = new String(receivePacket.getData(), 0,
            receivePacket.getLength());

        /* Måske ikke relevant?
        InetAddress clientIPAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();

        String client_address = clientIPAddress.getHostAddress() + ":" + clientPort;
        */

        try
        {
          String[] parts = request.split(":");
          SensorType type = SensorType.valueOf(parts[0]);
          double value = Double.parseDouble(parts[1]);

          blindsService.sensorData(type, value);
        }
        catch (IllegalArgumentException e)
        {
          System.out.println("Could not read packet: " + e.getMessage());
          Logger.getInstance()
              .log("ERROR", "Could not read packet: " + e.getMessage());
        }
      }
    }
    catch (IOException e)
    {
      if (serverSocket != null && !serverSocket.isClosed())
      {
        Logger.getInstance().log("ERROR", e.getMessage());
        throw new RuntimeException(e);
      }
      // Forventet lukning via stop()
    }
  }
}