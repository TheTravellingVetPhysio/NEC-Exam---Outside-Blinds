package simulator;

import model.SensorType;

import java.util.Random;

public class WindSimulator implements SensorSimulator
{
  private final Random random = new Random();
  private double currentValue = 5;

  @Override public double newValue()
  {
    currentValue += (random.nextDouble() - 0.5) * 3.0;
    currentValue = Math.clamp(currentValue, 0.0, 50.0);
    return Math.round(currentValue * 10.0) / 10.0;
  }

  @Override public SensorType getType()
  {
    return SensorType.WIND;
  }

}
