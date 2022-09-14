package com.example.Sensor.controllers;


import com.example.Sensor.DTO.MeasurementDTO;
import com.example.Sensor.entities.Measurement;
import com.example.Sensor.entities.Sensor;
import com.example.Sensor.services.MeasurementService;
import com.example.Sensor.services.SensorService;
import com.example.Sensor.util.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("measurements")
public class MeasurementController {

    private final MeasurementService measurementService;

    private final ModelMapper modelMapper;

    private final SensorService sensorService;

    @Autowired
    public MeasurementController(MeasurementService measurementService, ModelMapper modelMapper, SensorService sensorService) {
        this.measurementService = measurementService;
        this.modelMapper = modelMapper;
        this.sensorService = sensorService;
    }

    @GetMapping()
    public List<MeasurementDTO> getMeasurements() {
        return measurementService.findAll().stream().map(this::convertToDto)
                .collect(Collectors.toList());
    }


    @GetMapping("/rainyDaysCount")
    public Long countRainyDays() {
        return getMeasurements().stream().filter(MeasurementDTO::isRaining).count();
    }


    @PostMapping("/add")
    public ResponseEntity<HttpStatus> addMeasurement(@RequestBody @Valid MeasurementDTO measurementDTO,
                                                     BindingResult bindingResult) {


        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error :
                    errors) {
                errorMessage.append(error.getField())
                        .append("-").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new MeasurementException(errorMessage.toString());
        }


        measurementService.save(convertToMeasurement(measurementDTO));

        return ResponseEntity.ok(HttpStatus.OK);
    }


    private MeasurementDTO convertToDto(Measurement measurement) {
        return modelMapper.map(measurement, MeasurementDTO.class);
    }

    private Measurement convertToMeasurement(MeasurementDTO measurementDTO) {
        Measurement measurement = new Measurement();

        String name = measurementDTO.getSensor().getName();

        Sensor sensor = sensorService.findByName(name).orElseThrow(SensorNotFoundException::new);

        measurement.setValue(measurementDTO.getValue());
        measurement.setRaining(measurementDTO.isRaining());
        measurement.setSensor(sensor);
        measurement.setSensorName(sensor.getName());


        return measurement;

    }


    @ExceptionHandler
    private ResponseEntity<SensorError> handleException(SensorNotFoundException exception) {

        SensorError response = new SensorError(
                "Sensor wasn't found",
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);//status 404
    }

    @ExceptionHandler
    private ResponseEntity<MeasurementError> handleException(MeasurementException exception) {

        MeasurementError response = new MeasurementError(
                exception.getMessage(),
                System.currentTimeMillis()
        );

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);//status 400
    }


}
