package shared.listener;

import model.BlindsStatus;



// Dummy UI-listener - bruges til integration testing..
public class BlindsUIListenerService implements BlindsUIListener
{
  public void onBlindsChanged(BlindsStatus status)
  {
  }

  public void onSensorUpdated(double temperature, double sun,
      double wind)
  {
  }
}
