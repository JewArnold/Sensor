package com.example.Sensor.DTO;

import com.example.Sensor.entities.Sensor;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class MeasurementDTO {

    @NotNull(message = "Value shouldn't be null")
    @Min(value = -100, message = "Minimal value is -100")
    @Max(value = 100, message = "Maximal value is 100" )
    private double value;

    @NotNull(message = "Raining shouldn't be empty")
    private boolean raining;

    @NotNull(message = "Sensor shouldn't be empty")
    private Sensor sensor;

}
