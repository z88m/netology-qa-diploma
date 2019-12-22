package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import ru.netology.pages.CreditPayPage;
import ru.netology.pages.PayPage;
import ru.netology.pages.StartPage;
import ru.netology.testUtils.TestDataGenerator;
import ru.netology.testUtils.TestSQLHelper;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Tests {

    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    void cleanTables() throws SQLException {
        TestSQLHelper.cleanTables();
    }

    @Test
    @DisplayName("1. Успешная оплата валидной картой")
    void shouldConfirmPayWithApprovedCard() throws SQLException {
        StartPage page = new StartPage();
        PayPage payPage = page.payButtonClick();
        payPage.inputData(TestDataGenerator.getApprovedCard());
        payPage.continueButtonClick();
        payPage.checkNotificationApprovedVisible();
        //payPage.checkNotificationDeclinedNotVisible(); //достаточно одной позитивной проверки
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(payPage.getDbTable()));
    }

    @Test
    @DisplayName("2. Успешная оплата кредитом с валидной картой")
    void shouldConfirmCreditPayWithApprovedCard() throws SQLException {
        StartPage page = new StartPage();
        CreditPayPage creditPayPage = page.creditPayButtonClick();
        creditPayPage.inputData(TestDataGenerator.getApprovedCard());
        creditPayPage.continueButtonClick();
        creditPayPage.checkNotificationApprovedVisible();
        //creditPayPage.checkNotificationDeclinedNotVisible(); //достаточно одной позитивной проверки
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(creditPayPage.getDbTable()));
    }

    @Test
    @DisplayName("3. Отказ оплаты при невалидной карте")
    void shouldNotConfirmPayWithDeclinedCard() throws SQLException {
        StartPage page = new StartPage();
        PayPage payPage = page.payButtonClick();
        payPage.inputData(TestDataGenerator.getDeclinedCard());
        payPage.continueButtonClick();
        payPage.checkNotificationDeclinedVisible();
        //payPage.checkNotificationApprovedNotVisible(); //достаточно одной позитивной проверки
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(payPage.getDbTable()));
    }

    @Test
    @DisplayName("4. Отказ оплаты кредитом при невалидной карте")
    void shouldNotConfirmCreditPayWithDeclinedCard() throws SQLException {
        StartPage page = new StartPage();
        CreditPayPage creditPayPage = page.creditPayButtonClick();
        creditPayPage.inputData(TestDataGenerator.getDeclinedCard());
        creditPayPage.continueButtonClick();
        creditPayPage.checkNotificationDeclinedVisible();
        // creditPayPage.checkNotificationApprovedNotVisible();  //достаточно одной позитивной проверки
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(creditPayPage.getDbTable()));
    }
}