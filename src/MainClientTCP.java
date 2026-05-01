import client.ClientSocket;
import client.ClientSocketManagerTCP;

// Kun til test - det hele startes op via RunApp
public class MainClientTCP
{
  public static void main(String[] args)
  {
    ClientSocketManagerTCP socket = new ClientSocketManagerTCP("localhost", 6790);
    socket.receiveCommand();
  }
}