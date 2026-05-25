import client.BlindsClient;
import client.ClientSocketManagerTCP;

// Kun til test - det hele startes op via RunApp
public class MainClientTCP
{
  public static void main(String[] args)
  {
    String clientName = "BlindClient-" + System.currentTimeMillis();

    BlindsClient clientSocket = new ClientSocketManagerTCP("localhost", 6790, clientName);
    clientSocket.receiveCommand();
  }
}