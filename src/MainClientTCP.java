import client.ClientSocket;
import client.ClientSocketManagerTCP;

public class MainClientTCP
{
  public static void main(String[] args)
  {
//    create client socket
    ClientSocket socket = new ClientSocketManagerTCP("LocalHost", 6789);



  }
}
