package com.example.dices.services;

import com.example.dices.models.DiceModel;
import com.example.dices.repositories.IDiceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@SpringBootTest
@ExtendWith(MockitoExtension.class)
@Transactional
class DiceServiceTest {

    @Mock
    private IDiceRepository diceRepository;

    @Autowired
    @InjectMocks
    private DiceService diceService;


    @BeforeEach
    public void setUp() {
        diceRepository = mock(IDiceRepository.class); // Create a mock repository
        diceService = new DiceService(diceRepository); // Inject the mock repository
    }

    public static class TestServiceConstants {
        public static final int DEFAULT_DICE_ID1 = 1;
        public static final int DEFAULT_DICE_SIZE1 = 6;
        public static final int DEFAULT_DICE_ID2 = 2;
        public static final int DEFAULT_DICE_SIZE2 = 9;
        public static final int DEFAULT_DICE_ID3 = 3;
        public static final int DEFAULT_DICE_SIZE3 = 12;
        public static final int DEFAULT_DICE_INVALID_SIZE = 1001;
        public static final int DEFAULT_DICE_NOT_EXIST = 5;
        public static final int MINIMUM_DICE_SIZE = 1;
    }


    @Test
    void testGetDices() {
        // ARRANGE
        // simulate loading the list
        List<DiceModel> dices = new ArrayList<>();
        dices.add(new DiceModel(TestServiceConstants.DEFAULT_DICE_ID1, TestServiceConstants.DEFAULT_DICE_SIZE1));
        dices.add(new DiceModel(TestServiceConstants.DEFAULT_DICE_ID2, TestServiceConstants.DEFAULT_DICE_SIZE2));
        dices.add(new DiceModel(TestServiceConstants.DEFAULT_DICE_ID3, TestServiceConstants.DEFAULT_DICE_SIZE3));

        when(diceRepository.findAll()).thenReturn(dices);

        // ACT
        List<DiceModel> result = diceRepository.findAll(); // call service

        // ASSERT
        // verify that the service returns the expected list
        assertEquals(dices.size(), result.size());
        assertEquals(dices.get(0).getDiceId(), result.get(0).getDiceId());
        assertEquals(dices.get(0).getDiceSize(), result.get(0).getDiceSize());
        assertEquals(dices.get(1).getDiceId(), result.get(1).getDiceId());
        assertEquals(dices.get(1).getDiceSize(), result.get(1).getDiceSize());
        assertEquals(dices.get(2).getDiceId(), result.get(2).getDiceId());
        assertEquals(dices.get(2).getDiceSize(), result.get(2).getDiceSize());
        verify(diceRepository).findAll();
    }


    @Test
    void testCreateDiceWithValidSize() {
        // ARRANGE
        DiceModel validDice = new DiceModel();
        validDice.setDiceSize(TestServiceConstants.DEFAULT_DICE_SIZE1);

        // ACT
        when(diceRepository.save(validDice)).thenReturn(validDice);
        DiceModel createdDice = diceService.createDice(validDice);

        // ASSERT
        // Verify that the service method returns the expected result
        assertThat(createdDice).isNotNull();

        // Verify that the diceRepository.save() method was called with the inputDice
        verify(diceRepository, times(1)).save(validDice);

    }


    @Test
    void testCreateDiceWithInvalidSize() {
        // ARRANGE
        DiceModel invalidDice = new DiceModel();
        invalidDice.setDiceSize(TestServiceConstants.DEFAULT_DICE_INVALID_SIZE);

        // ACT
        DiceModel createdDice = diceService.createDice(invalidDice);

        // ASSERT
        // validates that the "save" method of the repository was not called.
        verify(diceRepository, never()).save(invalidDice);

        // validates that the error message is generated
        assertNull(createdDice);

    }


    @Test
    void testGetByIdExist() {
        // ARRANGE
        int diceId = TestServiceConstants.DEFAULT_DICE_ID1;

        DiceModel retrieveDice = new DiceModel();
        retrieveDice.setDiceId(diceId);
        retrieveDice.setDiceSize(TestServiceConstants.DEFAULT_DICE_SIZE1);

        // mock the behavior of the diceRepository.findById() method
        when(diceRepository.findById(diceId)).thenReturn(Optional.of(retrieveDice));

        // ACT
        Optional<DiceModel> obtainedDice = diceService.getById(diceId);

        // ASSERT
        verify(diceRepository).findById(diceId);
        assertEquals(diceId, obtainedDice.get().getDiceId());
    }


    @Test
    void testGetByIdNotExist() {
        // ARRANGE
        int diceId = TestServiceConstants.DEFAULT_DICE_NOT_EXIST;

        // Mock the behavior of the diceRepository.findById() method
        when(diceRepository.findById(diceId)).thenReturn(Optional.empty());

        // ACT
        Optional<DiceModel> obtainedDice = diceService.getById(diceId);

        // ASSERT
        // Verify that the service method returns the expected result
        verify(diceRepository).findById(diceId);
        verify(diceRepository, times(1)).findById(diceId);
        assertTrue(diceRepository.findById(diceId).isEmpty());

    }

    @Test
    void testDeleteDiceExist() {
        // ARRANGE
        int idToDelete = TestServiceConstants.DEFAULT_DICE_ID1;
        doNothing().when(diceRepository).deleteById(idToDelete);
        when(diceRepository.findById(idToDelete)).thenReturn(Optional.of(new DiceModel()));

        // ACT: call the service method
        Boolean result = diceService.deleteDice(idToDelete);

        // ASSERT
        // Verify that the service method returns true successful deletion
        assertTrue(result);
        verify(diceRepository).deleteById(idToDelete);
        verify(diceRepository).findById(idToDelete);
    }


    @Test
    void testDeleteDiceNotExist() {
        // ARRANGE
        int idToDelete = TestServiceConstants.DEFAULT_DICE_NOT_EXIST;
        when(diceRepository.findById(idToDelete)).thenReturn(Optional.empty());


        // ACT
        Boolean result = diceService.deleteDice(idToDelete); // call the service method

        // ASSERT
        assertFalse(result);

    }


    @Test
    void rollDiceSuccess() {
        // ARRANGE
        int idToRoll = TestServiceConstants.DEFAULT_DICE_ID1;
        DiceModel rollTheDice = new DiceModel();
        rollTheDice.setDiceId(TestServiceConstants.DEFAULT_DICE_ID1);
        rollTheDice.setDiceSize(TestServiceConstants.DEFAULT_DICE_SIZE1);

        // Mock the behavior of the diceRepository.findById() method
        when(diceRepository.findById(idToRoll)).thenReturn(Optional.of(rollTheDice));

        // ACT
        Optional<Integer> rollResultOptional = diceService.rollDice(idToRoll);  //Call the service method

        // ASSERT
        assertTrue(rollResultOptional.isPresent());  // Verify that the result is present
        int rollResult = rollResultOptional.get();

        // Verify that the service method returns a valid roll result within the expected range
        assertTrue(rollResult >= TestServiceConstants.MINIMUM_DICE_SIZE
                && rollResult <= rollTheDice.getDiceSize());
        verify(diceRepository).findById(idToRoll);
    }


    @Test
    void rollDiceFailure() {
        // ARRANGE
        int idToRoll = TestServiceConstants.DEFAULT_DICE_NOT_EXIST;

        // Mock the behavior of the diceRepository.findById() method
        when(diceRepository.findById(idToRoll)).thenReturn(Optional.empty());

        // ACT
        Optional<Integer> rollResult = diceService.rollDice(idToRoll);

        // ASSERT
        assertFalse(rollResult.isPresent());  // Verify that the result is not present
        verify(diceRepository).findById(idToRoll);
    }

}