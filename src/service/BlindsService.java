package service;

import model.SensorType;

import java.util.LinkedList;
import java.util.Queue;

public class BlindsService
{
  private boolean temperature;
  private boolean sun;
  private boolean wind;

  // Grænseværdier
  private static final double TEMPERATURE_LIMIT = 25; // temperatur i celcius
  private static final double SUN_LIMIT = 50000; // lux
  private static final double WIND_LIMIT = 10; // m/s

  // Vindhistorik (for at undgå høje vindstød)
  private static final int WIND_HISTORY_SIZE = 30; // input 1 gang/minuttet
  private final Queue<Double> windHistory = new LinkedList<>();   // Queue er et interface, der virker med "First in, first out", så en "automatisk" udskiftende arrayliste..

  public void sensorData(SensorType type, double value)
  {
    switch (type)
    {
      case TEMPERATURE -> temperature = isTemperatureTooHigh(value);
      case SUN -> sun = isSunTooStrong(value);
      case WIND -> wind = isWindTooStrong(value);
    }
  }

  public boolean isBlindsDownAutomatic()
  {
    if (wind)
    {
      return false;
    }

    return false;
  }

  private boolean isTemperatureTooHigh(Double value)
  {
    return value > TEMPERATURE_LIMIT;
  }

  private boolean isSunTooStrong(Double value)
  {
    return value > SUN_LIMIT;
  }

  private boolean isWindTooStrong(Double value)
  {
    if (windHistory.size() >= WIND_HISTORY_SIZE)
    {
      windHistory.poll(); // fjern ældste fra køen
    }
    windHistory.add(value);
    return
        windHistory.stream().mapToDouble(Double::doubleValue).max().orElse(0.0)
            > WIND_LIMIT;
  }
}
