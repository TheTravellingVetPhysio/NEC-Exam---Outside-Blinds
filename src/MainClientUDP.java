import client.ClientSocketManagerUDP;
import model.SensorType;

// Kun til test - det hele startes op via RunApp
public class MainClientUDP
{
  public static void main(String[] args) throws InterruptedException
  {
    ClientSocketManagerUDP socket = new ClientSocketManagerUDP("localhost", 6789);

    while (true)
    {
      socket.send(SensorType.TEMPERATURE, 26.5);
      socket.send(SensorType.SUN, 60000.0);
      socket.send(SensorType.WIND, 5.0);

      Thread.sleep(10000); // Send én gang pr 10 sekunder
    }
  }
}