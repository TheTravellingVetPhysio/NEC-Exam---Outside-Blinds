import model.DTO.SensorReadingDTO;
import server.ServerSocketManagerTCP;
import server.ServerSocketManagerUDP;
import service.BlindsService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

// Bruges kun til isoleret test af UDP-serveren.
// Det hele startes normalt via RunApp.
public class MainServerUDP
{
  public static void main(String[] args)
  {
    BlockingQueue<SensorReadingDTO> queue = new ArrayBlockingQueue<>(100);
    BlindsService blindsService = new BlindsService();
    ServerSocketManagerTCP tcpManager = new ServerSocketManagerTCP(6790, null);
    new ServerSocketManagerUDP(6789, queue, tcpManager);
  }
}