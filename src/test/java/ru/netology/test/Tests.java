package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.pages.CreditPayPage;
import ru.netology.pages.PayPage;
import ru.netology.testUtils.Cards;
import ru.netology.testUtils.TestDataGenerator;
import ru.netology.testUtils.TestSQLHelper;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    private Cards approvedCard = new Cards();
    private Cards declinedCard = new Cards();

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
        approvedCard.setNumber(TestDataGenerator.approvedCardNumber());
        approvedCard.setMonth(TestDataGenerator.month());
        approvedCard.setYear(TestDataGenerator.currentYearPlusOrMinus(2));
        approvedCard.setCardholder(TestDataGenerator.cardholder());
        approvedCard.setCvc(TestDataGenerator.cvc());

        declinedCard.setNumber(TestDataGenerator.declinedCardNumber());
        declinedCard.setMonth(TestDataGenerator.month());
        declinedCard.setYear(TestDataGenerator.currentYearPlusOrMinus(2));
        declinedCard.setCardholder(TestDataGenerator.cardholder());
        declinedCard.setCvc(TestDataGenerator.cvc());
    }

    @AfterEach
    void cleanTables() throws SQLException {
        TestSQLHelper.cleanTables();
    }

    @Test
    @DisplayName("1. Успешная оплата валидной картой")
    void shouldConfirmPayWithApprovedCard() throws SQLException {
        PayPage payPage = PayPage.selectPayPage(approvedCard);
        payPage.checkOperationApproved();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(payPage.getDbTable()));
    }

    @Test
    @DisplayName("2. Успешная оплата кредитом с валидной картой")
    void shouldConfirmCreditPayWithApprovedCard() throws SQLException {
        CreditPayPage creditPayPage = CreditPayPage.selectCreditPayPage(approvedCard);
        creditPayPage.checkOperationApproved();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(creditPayPage.getDbTable()));
    }

   @Test
    @DisplayName("3. Отказ оплаты при невалидной карте")
    void shouldNotConfirmPayWithDeclinedCard() throws SQLException {
        PayPage payPage = PayPage.selectPayPage(declinedCard);
        payPage.checkOperationDeclined();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(payPage.getDbTable()));
    }

    @Test
    @DisplayName("4. Отказ оплаты кредитом при невалидной карте")
    void shouldNotConfirmCreditPayWithDeclinedCard() throws SQLException {
        CreditPayPage creditPayPage = CreditPayPage.selectCreditPayPage(declinedCard);
        creditPayPage.checkOperationDeclined();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(creditPayPage.getDbTable()));
    }
}