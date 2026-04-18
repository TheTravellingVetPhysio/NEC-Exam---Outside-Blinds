package client;

import model.SensorType;

public interface SensorClient extends ClientSocket {
  void send(SensorType type, double value);
}
