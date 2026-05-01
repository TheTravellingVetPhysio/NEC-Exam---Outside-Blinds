package presentation.viewmodel;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import model.BlindsStatus;
import service.BlindsService;
import shared.listener.BlindsUIListener;

public class MainViewModel implements BlindsUIListener
{
  private final BlindsService blindsService;

  private final StringProperty temperatureText = new SimpleStringProperty("0.0 °C");
  private final StringProperty sunText = new SimpleStringProperty("0 lux");
  private final StringProperty windText = new SimpleStringProperty("0.0 m/s");
  private final StringProperty statusText = new SimpleStringProperty("CLOSED");
  private final StringProperty modeText = new SimpleStringProperty("AUTOMATIC");

  public MainViewModel(BlindsService blindsService)
  {
    this.blindsService = blindsService;
  }

  public void onManualUp()
  {
    blindsService.setManualUp();
  }
  public void onManualDown()
  {
    blindsService.setManualDown();
  }
  public void onAutomatic()
  {
    blindsService.setAutomatic();
  }

  @Override
  public void onBlindsChanged(BlindsStatus status)
  {
    Platform.runLater(() -> {
      statusText.set(status == BlindsStatus.CLOSED ? "CLOSED" : "OPEN");
      modeText.set(blindsService.getMode().toString());
    });
  }

  @Override
  public void onSensorUpdated(double temperature, double sun, double wind)
  {
    System.out.println("onSensorUpdated kaldt: " + temperature); // <-- tilføj denne
    Platform.runLater(() -> {
      temperatureText.set(String.format("%.1f °C", temperature));
      sunText.set(String.format("%.0f lux", sun));
      windText.set(String.format("%.1f m/s", wind));
    });
  }

  // Getters til binding i Controller
  public StringProperty temperatureTextProperty() { return temperatureText; }
  public StringProperty sunTextProperty()         { return sunText; }
  public StringProperty windTextProperty()        { return windText; }
  public StringProperty statusTextProperty()      { return statusText; }
  public StringProperty modeTextProperty()        { return modeText; }
}