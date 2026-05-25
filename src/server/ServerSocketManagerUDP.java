package server;

import model.DTO.SensorReadingDTO;
import model.SensorType;
import shared.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.BlockingQueue;

public class ServerSocketManagerUDP
{
  private final int port;
  private DatagramSocket serverSocket;
  private final BlockingQueue<SensorReadingDTO> queue;

  private final int DATAGRAM_SIZE = 32;

  public ServerSocketManagerUDP(int port, BlockingQueue<SensorReadingDTO> queue)
  {
    this.port = port;
    this.queue = queue;

    new Thread(this::run, "UDP-Producer").start();
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

        try
        {
          String[] parts = request.split(":");

          if (parts.length != 2)
          {
            Logger.getInstance().log("ERROR", "Invalid UDP packet format: " + request);
            continue;
          }

          SensorType type = SensorType.valueOf(parts[0]);
          double value = Double.parseDouble(parts[1]);

          queue.put(new SensorReadingDTO(type, value));
        }
        catch (IllegalArgumentException e)
        {
          System.out.println("Could not read packet: " + e.getMessage());
          Logger.getInstance()
              .log("ERROR", "Could not read packet: " + e.getMessage());
        }
        catch (InterruptedException e)
        {
          System.out.println("Could not read packet: " + e.getMessage());
          Logger.getInstance()
              .log("ERROR", "Could not read packet: " + e.getMessage());
          throw new RuntimeException(e);
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