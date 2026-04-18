package simulator;

import client.ClientSocketManagerUDP;

import java.util.List;

public class SensorSimulatorClient
{
  private final List<SensorSimulator> sensorSimulators = List.of(
      new TemperatureSimulator(),
      new SunSimulator(),
      new WindSimulator()
  );

  public void start() {
    for (SensorSimulator sim : sensorSimulators) {
      new Thread(() -> {
        ClientSocketManagerUDP client = new ClientSocketManagerUDP("localhost", 5000);
        while (true) {
          try {
            client.send(sim.getType(), sim.newValue());
            Thread.sleep(2000);
          } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            break;
          }
        }
      }).start();
    }
  }
}
