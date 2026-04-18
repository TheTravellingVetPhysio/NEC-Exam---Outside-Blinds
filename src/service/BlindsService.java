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
  private static final double WIND_GUST_LIMIT = 13;

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

  private void isTemperatureTooHigh(value)

}
