package shared.listener;

import model.BlindsStatus;
import server.ServerSocketManagerTCP;
import service.BlindsService;

// Sender TCP kommando og opdaterer sensorvisning
public class BlindsStateListenerService implements BlindsStateListener
{
  private final ServerSocketManagerTCP tcp;
  private BlindsServiceUIListener blindsServiceUIListener;
  private final BlindsService blindsService;
  private boolean lastState;

  public BlindsStateListenerService(ServerSocketManagerTCP tcp,
                                    BlindsServiceUIListener blindsServiceUIListener, BlindsService blindsService)
  {
    this.tcp                     = tcp;
    this.blindsServiceUIListener = blindsServiceUIListener;
    this.blindsService           = blindsService;
  }

  @Override public void onStateChanged(boolean blindsDown)
  {
    BlindsStatus status = blindsDown ? BlindsStatus.DOWN : BlindsStatus.UP;

    if (blindsDown != lastState)
    {
      tcp.sendCommand(status);
      lastState = blindsDown;
    }

    if (blindsServiceUIListener != null)
    {
      blindsServiceUIListener.onBlindsChanged(status);
      blindsServiceUIListener.onSensorUpdated(blindsService.getTemperature(), blindsService.getSun(),
                                              blindsService.getWind());
    }
  }
}