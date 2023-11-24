package com.example.dices.services;

import com.example.dices.models.DiceModel;
import com.example.dices.repositories.IDiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class DiceService {
    private final IDiceRepository diceRepository;

    @Autowired
    public DiceService(IDiceRepository diceRepository) {
        this.diceRepository = diceRepository;
    }

    // show list of created dices
    public List<DiceModel> getDices() {
        return (List<DiceModel>) diceRepository.findAll();
    }


    // create dice in the database
    public DiceModel createDice(DiceModel dice) {
        diceRepository.save(dice);
        return dice;
    }


    // shows the specific information of the id that comes in the parameter
    public DiceModel getById(Integer diceId) {

        Optional<DiceModel> optionalDice = diceRepository.findById(diceId);
        if (optionalDice.isPresent()) {
            DiceModel diceModel;
            diceModel = optionalDice.get();
            return diceModel;

        } else return null;
    }


    // delete the record of the id that enters by parameter
    public Boolean deleteDice(Integer id) {
        Optional<DiceModel> optionalDice = diceRepository.findById(id);

        if (optionalDice.isPresent()) {
            diceRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }


    // roll dice
    public Optional<Integer> rollDice(Integer id) {
        Optional<DiceModel> optionalDice = diceRepository.findById(id);

        if (optionalDice.isPresent()) {
            DiceModel diceModel = optionalDice.get();
            int diceSize = diceModel.getDiceSize();
            Random random = new Random();
            return Optional.of(random.nextInt(diceSize) + 1);

        } else {
            return Optional.empty();
        }
    }

}


