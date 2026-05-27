package client;

import model.BlindsStatus;
import shared.listener.BlindsServiceUIListener;

public interface BlindsClient extends ClientSocket
{
  void send(BlindsStatus status);
  void receiveCommand();
  void addListener(BlindsServiceUIListener listener);
}
