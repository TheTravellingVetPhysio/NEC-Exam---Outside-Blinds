package service;

import model.BlindsMode;
import model.SensorType;
import shared.listener.BlindsStateListener;

import java.util.LinkedList;
import java.util.Queue;

public class BlindsService
{
  private BlindsStateListener listener;

  private boolean temperature;
  private boolean sun;
  private boolean wind;

  private double currentTemperature;
  private double currentSun;
  private double currentWind;

  // Grænseværdier
  private static final double TEMPERATURE_LIMIT = 25; // temperatur i celcius
  private static final double SUN_LIMIT = 50000; // lux
  private static final double WIND_LIMIT = 10; // m/s

  // Vindhistorik (for at undgå høje vindstød)
  private static final int WIND_HISTORY_SIZE = 30; // input 1 gang/minuttet
  private final Queue<Double> windHistory = new LinkedList<>();   // Queue er et interface, der virker med "First in, first out", så en "automatisk" udskiftende arrayliste..

  private BlindsMode mode = BlindsMode.AUTOMATIC;

  public void setListener(BlindsStateListener listener)
  {
    this.listener = listener;
  }

  private void notifyListener()
  {
    if (listener != null)
    {
      listener.onStateChanged(isBlindsDown());
    }
  }

  public void sensorData(SensorType type, double value)
  {
    switch (type)
    {
      case TEMPERATURE ->
      {
        currentTemperature = value;
        temperature        = isTemperatureTooHigh(value);
      }
      case SUN ->
      {
        currentSun = value;
        sun        = isSunTooStrong(value);
      }
      case WIND ->
      {
        currentWind = value;
        wind        = isWindTooStrong(value);
        if (wind)
          mode = BlindsMode.AUTOMATIC;
      }
    }
    notifyListener();
  }

  public boolean isBlindsDown()
  {   // metoden serveren skal kalde
    return switch (mode)
    {
      case MANUAL_DOWN -> true;
      case MANUAL_UP -> false;
      case AUTOMATIC -> isBlindsDownAutomatic();
    };
  }

  public boolean isBlindsDownAutomatic()
  {
    return sun && temperature
        && !wind;   // Persiennen kører kun ned hvis der er sol, det er varmt og der ikke er vind
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
    return windHistory.stream().mapToDouble(Double::doubleValue).max().orElse(0.0) > WIND_LIMIT;
  }

  // Getters
  public double getTemperature()
  {
    return currentTemperature;
  }

  public double getSun()
  {
    return currentSun;
  }

  public double getWind()
  {
    return currentWind;
  }

  public BlindsMode getMode()
  {
    return mode;
  }

  // Set mode
  public void setManualUp()
  {
    mode = BlindsMode.MANUAL_UP;
  }

  public void setManualDown()
  {
    if (!isWindTooStrong(currentWind))
    {
      mode = BlindsMode.MANUAL_DOWN;
    }
    else {
      mode = BlindsMode.AUTOMATIC;
    }
  }

  public void setAutomatic()
  {
    mode = BlindsMode.AUTOMATIC;
  }
}
