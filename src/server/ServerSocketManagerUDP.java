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
  private final BlockingQueue<SensorReadingDTO> queue;
  private final int port;
  private final int DATAGRAM_SIZE = 32;

  private final Logger logger = Logger.getInstance();

  public ServerSocketManagerUDP(int port, BlockingQueue<SensorReadingDTO> queue)
  {
    this.port = port;
    this.queue = queue;

    new Thread(this::run, "UDP-Producer").start();
  }

  public void run()
  {
    System.out.println("Starting UDP producer server...");

    try (DatagramSocket serverSocket = new DatagramSocket(port))
    {
      while (true)
      {
        byte[] receiveData = new byte[DATAGRAM_SIZE];
        DatagramPacket receivePacket =
            new DatagramPacket(receiveData, receiveData.length);

        System.out.println("Waiting for a new value from sensor.");
        serverSocket.receive(receivePacket);

        String request = new String(
            receivePacket.getData(),
            0,
            receivePacket.getLength()
        );

        try
        {
          String[] parts = request.split(":");

          if (parts.length != 2)
          {
            logger.log("ERROR", "Invalid UDP packet format: " + request);
            continue;
          }

          SensorType type = SensorType.valueOf(parts[0]);
          double value = Double.parseDouble(parts[1]);

          SensorReadingDTO reading = new SensorReadingDTO(type, value);

          queue.put(reading);

          logger.log(
              "INFO",
              "UDP producer added reading to queue: " + reading
          );
        }
        catch (IllegalArgumentException e)
        {
          logger.log(
              "ERROR",
              "Could not parse UDP packet: " + request
          );
        }
      }
    }
    catch (IOException e)
    {
      logger.log("ERROR", e.getMessage());
      throw new RuntimeException(e);
    }
    catch (InterruptedException e)
    {
      Thread.currentThread().interrupt();
      logger.log("ERROR", "UDP producer interrupted.");
    }
  }
}