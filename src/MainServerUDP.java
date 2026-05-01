import server.ServerSocketManagerTCP;
import server.ServerSocketManagerUDP;
import service.BlindsService;

// Bruges kun til isoleret test af UDP-serveren.
// Det hele startes normalt via RunApp.
public class MainServerUDP
{
  public static void main(String[] args)
  {
    BlindsService blindsService = new BlindsService();
    ServerSocketManagerTCP tcpManager = new ServerSocketManagerTCP(6790);
    new ServerSocketManagerUDP(6789, blindsService, tcpManager);
  }
}