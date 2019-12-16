package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.pages.CreditPayPage;
import ru.netology.pages.PayPage;
import ru.netology.testUtils.Card;
import ru.netology.testUtils.DataGenerator;
import ru.netology.testUtils.SQLHelper;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    private Card approvedCard = new Card();
    private Card declinedCard = new Card();

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        approvedCard.setNumber(DataGenerator.approvedCardNumber());
        approvedCard.setMonth(DataGenerator.month());
        approvedCard.setYear(DataGenerator.currentYearPlusOrMinus(2));
        approvedCard.setCardholder(DataGenerator.cardholder());
        approvedCard.setCvc(DataGenerator.cvc());

        declinedCard.setNumber(DataGenerator.declinedCardNumber());
        declinedCard.setMonth(DataGenerator.month());
        declinedCard.setYear(DataGenerator.currentYearPlusOrMinus(2));
        declinedCard.setCardholder(DataGenerator.cardholder());
        declinedCard.setCvc(DataGenerator.cvc());
    }

    @AfterEach
    void cleanTables() throws SQLException {
        SQLHelper.cleanTables();
    }

    @Test
    @DisplayName("1. Успешная оплата валидной картой")
    void shouldConfirmPayWithApprovedCard() throws SQLException {
        PayPage payPage = PayPage.selectPayPage(approvedCard);
        payPage.checkOperationApproved();
        assertEquals("APPROVED", SQLHelper.getOperationStatus(payPage.getDbTable()));
    }

    @Test
    @DisplayName("2. Успешная оплата кредитом с валидной картой")
    void shouldConfirmCreditPayWithApprovedCard() throws SQLException {
        CreditPayPage creditPayPage = CreditPayPage.selectCreditPayPage(approvedCard);
        creditPayPage.checkOperationApproved();
        assertEquals("APPROVED", SQLHelper.getOperationStatus(creditPayPage.getDbTable()));
    }

   @Test
    @DisplayName("3. Отказ оплаты при невалидной карте")
    void shouldNotConfirmPayWithDeclinedCard() throws SQLException {
        PayPage payPage = PayPage.selectPayPage(declinedCard);
        payPage.checkOperationDeclined();
        assertEquals("DECLINED", SQLHelper.getOperationStatus(payPage.getDbTable()));
    }

    @Test
    @DisplayName("4. Отказ оплаты кредитом при невалидной карте")
    void shouldNotConfirmCreditPayWithDeclinedCard() throws SQLException {
        CreditPayPage creditPayPage = CreditPayPage.selectCreditPayPage(declinedCard);
        creditPayPage.checkOperationDeclined();
        assertEquals("DECLINED", SQLHelper.getOperationStatus(creditPayPage.getDbTable()));
    }
}