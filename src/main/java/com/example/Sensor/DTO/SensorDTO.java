package com.example.Sensor.DTO;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class SensorDTO {
    @NotEmpty(message = "Sensor's name shouldn't be empty")
    @Size(min = 3, max = 30, message = "Sensor's name should be from 3 to 30 chars")
    private String name;

}
