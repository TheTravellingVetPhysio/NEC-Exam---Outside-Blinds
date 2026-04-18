package client;

import model.BlindsStatus;

public interface BlindsClient extends ClientSocket
{
  void send(BlindsStatus status);
  void receiveCommand();
}
