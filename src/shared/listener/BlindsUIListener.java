package shared.listener;

import model.BlindsStatus;

public interface BlindsUIListener
{
  void onBlindsChanged(BlindsStatus status);
}
