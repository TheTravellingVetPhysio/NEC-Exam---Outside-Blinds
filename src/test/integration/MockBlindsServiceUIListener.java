package test.integration;

import model.BlindsStatus;
import shared.listener.BlindsServiceUIListener;

public class MockBlindsServiceUIListener implements BlindsServiceUIListener
{

  @Override public void onBlindsChanged(BlindsStatus status)
  {

  }

  @Override public void onSensorUpdated(double temperature, double sun, double wind)
  {

  }
}
