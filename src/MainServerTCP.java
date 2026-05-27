import server.ServerSocketManagerTCP;

// Kun til test - det hele startes op via RunApp
public class MainServerTCP
{
  public static void main(String[] args)
  {
    new ServerSocketManagerTCP(6790);
  }

}
