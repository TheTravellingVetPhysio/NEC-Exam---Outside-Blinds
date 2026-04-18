package client;

import model.SensorType;

public interface ClientSocket
{
  void connect(String host, int port);
  void disconnect();
}
