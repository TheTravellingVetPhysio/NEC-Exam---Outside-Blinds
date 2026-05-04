package model.DTO;

import model.SensorType;

public record SensorReadingDTO(SensorType type,
                               double value)
{
}
