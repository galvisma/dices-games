package com.example.dices.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "tbl_dice")
public class DiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer diceId;

    private int diceSize;

    @Transient
    private int diceRoll;

    public DiceModel() {
    }

    public DiceModel(Integer diceId, int diceSize) {
        this.diceId = diceId;
        this.diceSize = diceSize;
    }

    public DiceModel(int diceSize) {
        this.diceSize = diceSize;
    }

    public Integer getDiceId() {
        return diceId;
    }

    public int getDiceSize() {
        return diceSize;
    }

    public int roll() {
        diceRoll = (int) (Math.random() * (this.diceSize) + 1);
        return diceRoll;
    }

    public void setDiceId(Integer diceId) {
        this.diceId = diceId;
    }

    public void setDiceSize(int diceSize) {
        this.diceSize = diceSize;
    }

    public void setDiceRoll(int diceRoll) {
        this.diceRoll = diceRoll;
    }
}
