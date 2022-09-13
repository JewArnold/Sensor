package com.example.Sensor.controllers;

import com.example.Sensor.DTO.SensorDTO;
import com.example.Sensor.entities.Sensor;
import com.example.Sensor.services.SensorService;
import com.example.Sensor.util.SensorError;
import com.example.Sensor.util.SensorIsAlreadyExistsException;
import com.example.Sensor.validators.SensorValidator;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("sensors")
public class SensorController {


    private final SensorValidator sensorValidator;
    private final ModelMapper modelMapper;
    private final SensorService sensorService;

    @Autowired
    public SensorController(SensorValidator sensorValidator, ModelMapper modelMapper, SensorService sensorService) {
        this.sensorValidator = sensorValidator;
        this.modelMapper = modelMapper;
        this.sensorService = sensorService;
    }

    @PostMapping("/registration")
    public ResponseEntity<HttpStatus> addSensor(@RequestBody @Valid SensorDTO sensorDTO,
                                                BindingResult bindingResult) {

        sensorValidator.validate(convertToSensor(sensorDTO), bindingResult);


        if (bindingResult.hasErrors()) { //если json невалидный
            //обрабатываем ошибку и отправляем json с инфо об ошибке
            StringBuilder errorMessage = new StringBuilder();
            List<FieldError> errors = bindingResult.getFieldErrors();
            for (FieldError error :
                    errors) {
                errorMessage.append(error.getField())
                        .append("-").append(error.getDefaultMessage())
                        .append(";");
            }
            throw new SensorIsAlreadyExistsException(errorMessage.toString());
        }

        sensorService.save(convertToSensor(sensorDTO));
        return ResponseEntity.ok(HttpStatus.OK);
    }


    private Sensor convertToSensor(SensorDTO sensorDTO) {

        return modelMapper.map(sensorDTO, Sensor.class);
    }


    @ExceptionHandler //обработчик исключений. В параметре явно указываем какое исключение ловим
    private ResponseEntity<SensorError> handleException(SensorIsAlreadyExistsException exception) {

        SensorError response = new SensorError( //объект, который создаем в случае перехвата исключения
                exception.getMessage(), //получаем сообщение, которое создали в методе create
                System.currentTimeMillis()  //текущее время в милисекундах
        );

        //преобазуем response в объект для передачи по сети, указваем статус ответа
        //в HTTP body будет отображено тело response и статус HttpStatus
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);//status 400
    }

}
