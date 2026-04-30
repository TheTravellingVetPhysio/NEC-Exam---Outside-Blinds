package simulator;

import model.SensorType;

import java.util.Random;

public class SunSimulator implements SensorSimulator
{
  private final Random random = new Random();
  private double currentValue = 30000;

  @Override public double newValue()
  {
    currentValue += (random.nextDouble() - 0.5) * 5000.0;
    currentValue = Math.clamp(currentValue, 0.0, 100000.0);
    return Math.round(currentValue);
  }

  @Override public SensorType getType()
  {
    return SensorType.SUN;
  }

}
