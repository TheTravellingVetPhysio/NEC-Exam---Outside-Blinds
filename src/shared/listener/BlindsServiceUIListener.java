package shared.listener;

import model.BlindsStatus;

public interface BlindsServiceUIListener
{
  void onBlindsChanged(BlindsStatus status);
  void onSensorUpdated(double temperature, double sun, double wind);
}
