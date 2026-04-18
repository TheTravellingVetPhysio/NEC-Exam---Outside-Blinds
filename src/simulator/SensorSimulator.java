package simulator;

import model.SensorType;

public interface SensorSimulator {
  double newValue();
  SensorType getType();
}