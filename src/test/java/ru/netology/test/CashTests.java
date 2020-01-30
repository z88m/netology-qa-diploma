package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import ru.netology.pages.StartPage;
import ru.netology.testUtils.TestDataGenerator;
import ru.netology.testUtils.TestSQLHelper;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CashTests {

    @BeforeAll
    static void setUpAll() throws SQLException {
        TestSQLHelper.cleanTables();
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
    @DisplayName("1. Купить. Успешная оплата разрешённой картой")
    void shouldConfirmPayWithApprovedCard() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @Test
    @DisplayName("2. Купить. Отказ в оплате запрещённой картой")
    void shouldNotConfirmPayWithDeclinedCard() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getDeclinedCard());
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/notExistCardNumber.cvs", numLinesToSkip = 1)
    @DisplayName("3. Купить. Отказ. Номер карты валиден, но не существует в системе")
    void shouldNotConfirmPayWithBadCard(String number, String message) {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData(number);
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()), message);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCardNumber.cvs", numLinesToSkip = 1)
    @DisplayName("4. Купить. Оплата проходит после исправления номера карты на валидный")
    void shouldPayProcessAfterRetypeNumCard(String number, String message) {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData(number);
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()), message);
        page.checkCardNumberFormatErrorHave();
        page.cleanNumberAndInputNewData(TestDataGenerator.approvedCardNumber());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkCardNumberFormatErrorNotHave();
    }

    @Test
    @DisplayName("5. Купить. Оплата не происходит. Карта просрочена на месяц")
    void shouldNotPayProcessWithOverdueMonth() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(-1));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrentByMonth(-1));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkDateErrorHave();
    }

    @Test
    @DisplayName("6. Купить. Оплата не происходит. Карта просрочена на год")
    void shouldNotPayProcessWithOverdueYear() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(0));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(-1));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkDateErrorHave();
    }

    @Test
    @DisplayName("7. Купить. Оплата не происходит. Срок действия карты больше 5 лет")
    void shouldNotPayProcessWithYearMore5FromCurrent() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(0));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(6));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkYearErrorHave();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongMonth.cvs", numLinesToSkip = 1)
    @DisplayName("8. Купить. Оплата проходит после исправления месяца")
    void shouldPayProcessWithCorrectionWrongMonth(String month, String message) {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(month);
        page.continueButtonClick();
        page.checkMonthDateErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()), message);
        page.cleanMonthAndInputNewData("12");
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkMonthDateErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongYear.cvs", numLinesToSkip = 1)
    @DisplayName("9. Купить. Оплата проходит после исправления года")
    void shouldPayProcessWithCorrectionWrongYear(String year, String message) {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData(year);
        page.continueButtonClick();
        page.checkYearFormatErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()), message);
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(2));
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkYearFormatErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCardholderName.cvs", numLinesToSkip = 1)
    @DisplayName("10. Купить. Оплата происходит после исправления имени владельца")
    void shouldPayProcessWithCorrectedCardholderName(String owner) {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData(owner);
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkOwnerFormatErrorHave();
        page.cleanOwnerAndInputNewData(TestDataGenerator.generateCardholder());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkOwnerFormatErrorNotHave();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCVV.cvs", numLinesToSkip = 1)
    @DisplayName("11. Купить. Оплата происходит после исправления CVC/CVV")
    void shouldPayProcessWithCorrectedCvc(String cvv, String message) {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData(cvv);
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()), message);
        page.checkCvcErrorHave();
        page.cleanCvcAndInputNewData(TestDataGenerator.generateCvc());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkCvcErrorNotHave();
    }
}