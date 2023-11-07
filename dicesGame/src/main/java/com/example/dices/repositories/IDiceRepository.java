package com.example.dices.repositories;

import com.example.dices.models.DiceModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IDiceRepository extends JpaRepository<DiceModel, Integer> {


}
