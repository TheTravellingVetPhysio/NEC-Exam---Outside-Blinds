package server;

import model.DTO.SensorReadingDTO;
import service.BlindsService;
import shared.logger.Logger;

import java.util.concurrent.BlockingQueue;

public class SensorReadingConsumer implements Runnable
{
  private final BlockingQueue<SensorReadingDTO> queue;
  private final BlindsService blindsService;

  private final Logger logger = Logger.getInstance();

  public SensorReadingConsumer(BlockingQueue<SensorReadingDTO> queue, BlindsService blindsService)
  {
    this.queue         = queue;
    this.blindsService = blindsService;
  }

  @Override public void run()
  {
    while (true)
    {
      try
      {
        SensorReadingDTO readingDTO = queue.take();

        logger.log("INFO", "Consumer processing reading: " + readingDTO);

        blindsService.sensorData(readingDTO.type(), readingDTO.value());
      }
      catch (InterruptedException e)
      {
        Thread.currentThread().interrupt();
        logger.log("ERROR", "Consumer interrupter.");
        break;
      }
    }
  }
}
