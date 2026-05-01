package shared.listener;

import model.BlindsStatus;

public interface BlindsUIListener
{
  void onBlindsChanged(BlindsStatus status);
  void onSensorUpdated(double temperature, double sun, double wind);
}
