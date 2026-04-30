package shared.listener;

import model.BlindsStatus;
import server.ServerSocketManagerTCP;

public class BlindsStateListenerService implements BlindsStateListener
{
  private final ServerSocketManagerTCP tcp;
  private boolean lastState;

  public BlindsStateListenerService(ServerSocketManagerTCP tcp)
  {
    this.tcp = tcp;
  }

  @Override
  public void onStateChanged(boolean blindsDown)
  {
    if (blindsDown != lastState)
    {
      tcp.sendCommand(
          blindsDown ? BlindsStatus.CLOSED : BlindsStatus.OPEN
      );
      lastState = blindsDown;
    }
  }
}