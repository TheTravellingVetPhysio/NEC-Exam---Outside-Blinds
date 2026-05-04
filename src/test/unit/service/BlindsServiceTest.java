package test.unit.service;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import model.SensorType;
import service.BlindsService;

public class BlindsServiceTest
{
  private BlindsService blindsService;

  @BeforeEach void setup()
  {
    blindsService = new BlindsService();
  }

  @Test void isAutomaticDown_whenAllConditionsAreMet_returnsTrue()
  {
    // Arrange
    blindsService.setAutomatic();
    blindsService.sensorData(SensorType.TEMPERATURE, 30); // LIMIT 25
    blindsService.sensorData(SensorType.SUN, 60000);  // LIMIT 50000
    blindsService.sensorData(SensorType.WIND, 3); // LIMIT 10

    // Act + Assert
    assertTrue(blindsService.isBlindsDownAutomatic());
  }

  @Test void isAutomaticDown_whenTemperatureIsTooLow_returnsFalse()
  {
    // Arrange
    blindsService.setAutomatic();
    blindsService.sensorData(SensorType.TEMPERATURE, 20); // LIMIT 25
    blindsService.sensorData(SensorType.SUN, 60000);  // LIMIT 50000
    blindsService.sensorData(SensorType.WIND, 3); // LIMIT 10

    // Act + Assert
    assertFalse(blindsService.isBlindsDownAutomatic());
  }

  @Test void isAutomaticDown_whenSunIsTooLow_returnsFalse()
  {
    // Arrange
    blindsService.setAutomatic();
    blindsService.sensorData(SensorType.TEMPERATURE, 30); // LIMIT 25
    blindsService.sensorData(SensorType.SUN, 40000);  // LIMIT 50000
    blindsService.sensorData(SensorType.WIND, 3); // LIMIT 10

    // Act + Assert
    assertFalse(blindsService.isBlindsDownAutomatic());
  }

  @Test void isAutomaticDown_whenWindIsTooHigh_returnsFalse()
  {
    // Arrange
    blindsService.setAutomatic();
    blindsService.sensorData(SensorType.TEMPERATURE, 30); // LIMIT 25
    blindsService.sensorData(SensorType.SUN, 60000);  // LIMIT 50000
    blindsService.sensorData(SensorType.WIND, 13); // LIMIT 10

    // Act + Assert
    assertFalse(blindsService.isBlindsDownAutomatic());
  }

  @Test void setManualDown_whenWindIsLow_returnsTrue()
  {
    // Arrange
    blindsService.sensorData(SensorType.TEMPERATURE, 0);
    blindsService.sensorData(SensorType.SUN, 0);
    blindsService.sensorData(SensorType.WIND, 3);

    // Act
    blindsService.setManualDown();

    // Assert
    assertTrue(blindsService.isBlindsDown());
  }

  @Test void setManualDown_whenWindIsHigh_returnsFalse()
  {
    // Arrange
    blindsService.sensorData(SensorType.TEMPERATURE, 0);
    blindsService.sensorData(SensorType.SUN, 0);
    blindsService.sensorData(SensorType.WIND, 13);

    // Act
    blindsService.setManualDown();

    // Assert
    assertFalse(blindsService.isBlindsDown());
  }

  @Test void setManualUp_overridesAutomatic_returnsIsBlindsDownFalse()
  {
    // Arrange
    blindsService.sensorData(SensorType.TEMPERATURE, 30);
    blindsService.sensorData(SensorType.SUN, 60000);
    blindsService.sensorData(SensorType.WIND, 3);

    // Act
    blindsService.setManualUp();

    // Assert
    assertFalse(blindsService.isBlindsDown());
  }

  // Fejl i UI med open/closed skyldes umiddelbart ikke Service-logikken
}
