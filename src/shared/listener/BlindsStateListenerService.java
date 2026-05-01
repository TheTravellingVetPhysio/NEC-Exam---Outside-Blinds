package shared.listener;

import model.BlindsStatus;
import server.ServerSocketManagerTCP;
import service.BlindsService;

public class BlindsStateListenerService implements BlindsStateListener
{
  private final ServerSocketManagerTCP tcp;
  private BlindsUIListener blindsUIListener;
  private final BlindsService blindsService;
  private boolean lastState;

  public BlindsStateListenerService(ServerSocketManagerTCP tcp,
      BlindsUIListener blindsUIListener, BlindsService blindsService)
  {
    this.tcp = tcp;
    this.blindsUIListener = blindsUIListener;
    this.blindsService = blindsService;
  }

  @Override public void onStateChanged(boolean blindsDown)
  {
    BlindsStatus status = blindsDown ? BlindsStatus.CLOSED : BlindsStatus.OPEN;

    if (blindsDown != lastState)
    {
      tcp.sendCommand(status);
      lastState = blindsDown;
    }

    if (blindsUIListener != null)
    {
      blindsUIListener.onBlindsChanged(status);
      blindsUIListener.onSensorUpdated(blindsService.getTemperature(),
          blindsService.getSun(), blindsService.getWind());
    }
  }
}