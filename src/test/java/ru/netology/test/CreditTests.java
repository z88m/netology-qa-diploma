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

public class CreditTests {

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
    @DisplayName("12. Купить в кредит. Успешная оплата кредитом с разрешённой картой")
    void shouldConfirmCreditPayWithApprovedCard() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @Test
    @DisplayName("13. Купить в кредит. Отказ в оплате кредитом с запрещённой картой")
    void shouldNotConfirmCreditPayWithDeclinedCard() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getDeclinedCard());
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/notExistCardNumber.cvs", numLinesToSkip = 1)
    @DisplayName("14. Купить в кредит. Отказ. Номер карты валиден, но не существует в системе")
    void shouldNotConfirmCreditPayWithBadCard(String number, String message) {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData(number);
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()), message);
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCardNumber.cvs", numLinesToSkip = 1)
    @DisplayName("15. Купить в кредит. Оплата проходит после исправления номера карты на валидный")
    void shouldCreditPayProcessAfterRetypeNumCard(String number) {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData(number);
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkCardNumberFormatErrorHave();
        page.cleanNumberAndInputNewData(TestDataGenerator.approvedCardNumber());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkCardNumberFormatErrorNotHave();
    }

    @Test
    @DisplayName("16. Купить в кредит. Оплата не происходит. Карта просрочена на месяц")
    void shouldNotCreditPayProcessWithOverdueMonth() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(-1));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrentByMonth(-1));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkDateErrorHave();
    }

    @Test
    @DisplayName("17. Купить в кредит. Оплата не происходит. Карта просрочена на год")
    void shouldNotCreditPayProcessWithOverdueYear() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(0));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(-1));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkDateErrorHave();
    }

    @Test
    @DisplayName("18. Купить в кредит. Оплата не происходит. Срок действия карты больше 5 лет")
    void shouldNotCreditPayProcessWithOverdueYearMore5FromCurrent() {
        StartPage page = StartPage.creditPayButtonClick();
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
    @DisplayName("19. Купить в кредит. Оплата проходит после исправления месяца")
    void shouldCreditPayProcessWithCorrectionWrongMonth(String month, String message) {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(month);
        page.continueButtonClick();
        page.checkMonthDateErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()), message);
        page.cleanMonthAndInputNewData("12");
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkMonthDateErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongYear.cvs", numLinesToSkip = 1)
    @DisplayName("20. Купить в кредит. Оплата проходит после исправления года")
    void shouldCreditPayProcessWithCorrectionWrongYear(String year, String message) {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData(year);
        page.continueButtonClick();
        page.checkYearFormatErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()), message);
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(2));
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkYearFormatErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCardholderName.cvs", numLinesToSkip = 1)
    @DisplayName("21. Купить в кредит. Купить. Оплата происходит после исправления имени владельца")
    void shouldCreditPayProcessWithCorrectedCardholderName(String owner, String message) {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData(owner);
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()), message);
        page.checkOwnerFormatErrorHave();
        page.cleanOwnerAndInputNewData(TestDataGenerator.generateCardholder());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkOwnerFormatErrorNotHave();
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/wrongCVV.cvs", numLinesToSkip = 1)
    @DisplayName("22. Купить в кредит. Оплата происходит после исправления CVC/CVV")
    void shouldCreditPayProcessWithCorrectedCvc(String cvv, String message) {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData(cvv);
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()), message);
        page.checkCvcErrorHave();
        page.cleanCvcAndInputNewData(TestDataGenerator.generateCvc());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkCvcErrorNotHave();
    }
}