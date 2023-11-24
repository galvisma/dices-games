package com.example.dices.controllers;

import com.example.dices.models.DiceModel;
import com.example.dices.models.JsonModel;
import com.example.dices.services.DiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("api/v1/dices")

public class DiceController {
    @Autowired
    private final DiceService diceService;

    public DiceController(DiceService diceService) {
        this.diceService = diceService;
    }

    public static class ControllerConstants {
        public static final int MINIMUM_DICE_SIZE = 1;
        public static final int MAXIMUM_DICE_SIZE = 1000;
        public static final String ERROR_NO_DATA = "There is no information to display";
        public static final String ERROR_NOT_FOUND = "Dice not found with ID: ";
        public static final String ERROR_INVALID_SIZE = "The dice size must be between 1 and 1000.";
        public static final String SUCCESSFUL_DELETED = "Successfully deleted dice with Id: ";
    }


    @GetMapping
    @ResponseBody
    public ResponseEntity<?> getDices() {
        List<DiceModel> diceList = diceService.getDices();

        if (diceList.isEmpty()) {
            String message = ControllerConstants.ERROR_NO_DATA;
            return ResponseEntity.status(HttpStatus.OK).body(message);
        }
        return ResponseEntity.ok(diceList);
    }

    @PostMapping
    public ResponseEntity<?> saveDice(@RequestBody DiceModel dice) {
        int validSize = dice.getDiceSize();
        if (validSize >= ControllerConstants.MINIMUM_DICE_SIZE &&
                validSize <= ControllerConstants.MAXIMUM_DICE_SIZE) {
            DiceModel createdDice = diceService.createDice(dice);
            return ResponseEntity.ok(createdDice);
        }
        String message = ControllerConstants.ERROR_INVALID_SIZE;
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(message);
    }

    @GetMapping(path = "/{id}")
    @ResponseBody
    public ResponseEntity<?> getDiceById(@PathVariable Integer id) {

        Optional<DiceModel> obtainedDice = diceService.getById(id);

        if (obtainedDice.isPresent()) {
            return ResponseEntity.ok(obtainedDice);
        }
        String message = ControllerConstants.ERROR_NOT_FOUND + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(message);
    }


    @PostMapping(path = "/{id}/rolls")
    @ResponseBody
    public ResponseEntity<?> calculateRandomNumber(@PathVariable Integer id) {
        Optional<Integer> roll = diceService.rollDice(id);

        if (roll.isPresent()) {
            JsonModel jsonModel = new JsonModel(id, roll);
            return ResponseEntity.ok(jsonModel);
        }
        String errorMessage = ControllerConstants.ERROR_NOT_FOUND + id;
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMessage);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<?> deleteDiceById(@PathVariable("id") Integer id) {
        if (diceService.deleteDice(id)) {
            return ResponseEntity.ok(ControllerConstants.SUCCESSFUL_DELETED + id);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ControllerConstants.ERROR_NOT_FOUND + id);
    }
}