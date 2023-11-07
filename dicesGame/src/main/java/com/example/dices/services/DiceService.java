package com.example.dices.services;

import com.example.dices.models.DiceModel;
import com.example.dices.repositories.IDiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

@Service
public class DiceService {
    private final IDiceRepository diceRepository;

    @Autowired
    public DiceService(IDiceRepository diceRepository) {
        this.diceRepository = diceRepository;
    }

    public static class ServiceConstants {
        public static final int MINIMUM_DICE_SIZE = 1;
        public static final int MAXIMUM_DICE_SIZE = 1000;

    }


    // show list of created dices
    public ArrayList<DiceModel> getDices() {
        return (ArrayList<DiceModel>) diceRepository.findAll();
    }


    // create dice in the database
    public ResponseEntity<?> createDice(DiceModel dice) {
        int validSize = dice.getDiceSize();
        if (validSize >= ServiceConstants.MINIMUM_DICE_SIZE &&
                validSize <= ServiceConstants.MAXIMUM_DICE_SIZE) {
            diceRepository.save(dice);
            return ResponseEntity.ok(dice);
        } else {
            return ResponseEntity.badRequest().build();
        }
    }


    // shows the specific information of the id that comes in the parameter
    public ResponseEntity<?> getById(Integer diceId) {

        Optional<DiceModel> optionalDice = diceRepository.findById(diceId);
        if (optionalDice.isPresent()) {
            DiceModel diceModel = optionalDice.get();
            return ResponseEntity.ok(diceModel);
        } else return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }


    // delete the record of the id that enters by parameter
    public Boolean deleteDice(Integer id) {
        try {
            diceRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    // roll dice
    public int rollDice(Integer id) {
        Optional<DiceModel> optionalDice = diceRepository.findById(id);

        if (optionalDice.isPresent()) {
            DiceModel diceModel = optionalDice.get();
            int diceSize = diceModel.getDiceSize();
            Random random = new Random();
            return random.nextInt(diceSize) + 1;
        } else {
            return 0;
        }
    }

}


