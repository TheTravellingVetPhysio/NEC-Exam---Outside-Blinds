package simulator;

import model.SensorType;

import java.util.Random;

public class TemperatureSimulator implements SensorSimulator {

  private final Random random = new Random();
  private double currentValue = 22.0;

  @Override
  public double newValue() {
    currentValue += (random.nextDouble() - 0.5) * 2.0;
    currentValue = Math.clamp(currentValue, 20.0, 25.0);
    return Math.round(currentValue * 10.0) / 10.0;
  }

  @Override
  public SensorType getType() { return SensorType.TEMPERATURE; }
}