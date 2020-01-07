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
    @DisplayName("1. Купить. Успешная оплата разрешённой картой")
    void shouldConfirmPayWithApprovedCard() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("2. Купить в кредит. Успешная оплата кредитом с разрешённой картой")
    void shouldConfirmCreditPayWithApprovedCard() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("3. Купить. Отказ в оплате запрещённой картой")
    void shouldNotConfirmPayWithDeclinedCard() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getDeclinedCard());
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("4. Купить в кредит. Отказ в оплате кредитом с запрещённой картой")
    void shouldNotConfirmCreditPayWithDeclinedCard() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getDeclinedCard());
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("5. Купить. Отказ. Номер карты не существует в системе")
    void shouldNotConfirmPayWithBadCard() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("4444444444444444");
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("6. Купить в кредит. Отказ. Номер карты не существует в системе")
    void shouldNotConfirmCreditPayWithBadCard() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("4444444444444444");
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("7. Купить. Оплата не происходит. Незаполнен номер карты")
    void shouldNotPayProcessWithoutNumCard() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCardNumberFormatErrorHave();
    }

    @Test
    @DisplayName("8. Купить. Оплата не происходит. Неполный номер карты")
    void shouldNotPayProcessWithShortNumCard() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("444444444444444");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCardNumberFormatErrorHave();
    }

    @Test
    @DisplayName("9. Оплата проходит после исправления номера карты на валидный")
    void shouldPayProcessAfterRetypeNumCard() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCardNumberFormatErrorHave();
        page.cleanNumberAndInputNewData(TestDataGenerator.approvedCardNumber());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCardNumberFormatErrorNotHave();

    }

    @Test
    @DisplayName("10. Купить в кредит. Оплата не происходит. Незаполнен номер карты")
    void shouldNotCreditPayProcessWithoutNumCard() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCardNumberFormatErrorHave();
    }

    @Test
    @DisplayName("11. Купить в кредит. Оплата не происходит. Неполный номер карты")
    void shouldNotCreditPayProcessWithShortNumCard() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("444444444444444");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCardNumberFormatErrorHave();
    }

    @Test
    @DisplayName("12. Купить в кредит. Оплата проходит после исправления номера карты на валидный")
    void shouldCreditPayProcessAfterRetypeNumCard() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCardNumberFormatErrorHave();
        page.cleanNumberAndInputNewData(TestDataGenerator.approvedCardNumber());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCardNumberFormatErrorNotHave();
    }

    @Test
    @DisplayName("13. Купить. Оплата не происходит. Незаполнен месяц")
    void shouldNotPayProcessWithoutMonth() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthFormatErrorHave();
    }

    @Test
    @DisplayName("14. Купить. Оплата не происходит. Месяц введён одной цифрой")
    void shouldNotPayProcessWithShortMonth() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("1");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthFormatErrorHave();
    }

    @Test
    @DisplayName("15. Купить. Оплата не происходит. Месяц 00")
    void shouldNotPayProcessWithMonth00() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("00");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("16. Купить. Оплата не происходит. Месяц 13")
    void shouldNotPayProcessWithMonth13() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("13");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("17. Купить в кредит. Оплата не происходит. Незаполнен месяц")
    void shouldNotCreditPayProcessWithoutMonth() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthFormatErrorHave();
    }

    @Test
    @DisplayName("18. Купить в кредит. Оплата не происходит. Месяц введён одной цифрой")
    void shouldNotCreditPayProcessWithShortMonth() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("1");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthFormatErrorHave();
    }

    @Test
    @DisplayName("19. Купить в кредит. Оплата не происходит. Месяц 00")
    void shouldNotCreditPayProcessWithMonth00() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("00");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("20. Купить в кредит. Оплата не происходит. Месяц 13")
    void shouldNotCreditPayProcessWithMonth13() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("13");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("21. Купить. Оплата не происходит. Год не указан")
    void shouldNotPayProcessWithoutYear() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkYearFormatErrorHave();
    }

    @Test
    @DisplayName("22. Купить. Оплата не происходит. Год введён одной цифрой")
    void shouldNotPayProcessWithShortYear() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("1");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkYearFormatErrorHave();
    }

    @Test
    @DisplayName("23. Купить  в кредит. Оплата не происходит. Год не указан")
    void shouldNotCreditPayProcessWithoutYear() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkYearFormatErrorHave();
    }

    @Test
    @DisplayName("24. Купить в кредит. Оплата не происходит. Год введён одной цифрой")
    void shouldNotCreditPayProcessWithShortYear() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("1");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkYearFormatErrorHave();
    }

    @Test
    @DisplayName("25. Купить. Оплата не происходит. Карта просрочена на месяц")
    void shouldNotPayProcessWithOverdueMonth() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(-1));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrentByMonth(-1));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("26. Купить в кредит. Оплата не происходит. Карта просрочена на месяц")
    void shouldNotCreditPayProcessWithOverdueMonth() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(-1));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrentByMonth(-1));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("27. Купить. Оплата не происходит. Карта просрочена на год")
    void shouldNotPayProcessWithOverdueYear() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(0));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrentByMonth(-1));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("28. Купить в кредит. Оплата не происходит. Карта просрочена на год")
    void shouldNotCreditPayProcessWithOverdueYear() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(0));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrentByMonth(-1));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("29. Купить. Оплата не происходит. Срок действия карты больше 5 лет")
    void shouldNotPayProcessWithYearMore5FromCurrent() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(0));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrentByMonth(6));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("30. Купить в кредит. Оплата не происходит. Срок действия карты больше 5 лет")
    void shouldNotCreditPayProcessWithOverdueYearMore5FromCurrent() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedMMFromCurrent(0));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrentByMonth(6));
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("31. Купить. Оплата проходит после исправления месяца")
    void shouldPayProcessWithCorrectionWrongMonth() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("13");
        page.continueButtonClick();
        page.checkMonthFormatErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.cleanMonthAndInputNewData("12");
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkMonthFormatErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("32. Купить в кредит. Оплата проходит после исправления месяца")
    void shouldCreditPayProcessWithCorrectionWrongMonth() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("13");
        page.continueButtonClick();
        page.checkMonthFormatErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.cleanMonthAndInputNewData("12");
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkMonthFormatErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("33. Купить. Оплата проходит после исправления года")
    void shouldPayProcessWithCorrectionWrongYear() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("1");
        page.continueButtonClick();
        page.checkYearFormatErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(2));
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkYearFormatErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("34. Купить в кредит. Оплата проходит после исправления года")
    void shouldCreditPayProcessWithCorrectionWrongYear() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("1");
        page.continueButtonClick();
        page.checkYearFormatErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.cleanMonthAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(2));
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkYearFormatErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
    }

    @Test
    @DisplayName("35. Купить. Оплата не происходит. Имя владельца не заполнено")
    void shouldNotPayProcessWithoutCardholderName() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkOwnerErrorHave();
    }

    @Test
    @DisplayName("36. Купить в кредит. Оплата не происходит. Имя владельца не заполнено")
    void shouldNotCreditPayProcessWithoutCardholderName() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkOwnerErrorHave();
    }

    @Test
    @DisplayName("37. Купить. Оплата не происходит. Имя владельца не валидно")
    void shouldNotPayProcessWithWrongCardholderName() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("Василий @№123");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkOwnerErrorHave();
    }

    @Test
    @DisplayName("38. Купить в кредит. Оплата не происходит. Имя владельца не валидно")
    void shouldNotCreditPayProcessWithWrongCardholderName() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("Василий @№123");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkOwnerErrorHave();
    }

    @Test
    @DisplayName("39. Купить. Оплата происходит после исправления имени владельца")
    void shouldPayProcessWithCorrectedCardholderName() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("Василий @№123");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkOwnerErrorHave();
        page.cleanOwnerAndInputNewData(TestDataGenerator.generateCardholder());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkOwnerErrorNotHave();
    }

    @Test
    @DisplayName("40. Купить в кредит. Купить. Оплата происходит после исправления имени владельца")
    void shouldCreditPayProcessWithCorrectedCardholderName() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("Василий @№123");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkOwnerErrorHave();
        page.cleanOwnerAndInputNewData(TestDataGenerator.generateCardholder());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkOwnerErrorNotHave();
    }

    @Test
    @DisplayName("41. Купить. Оплата не происходит. CVC/CVV не заполнено")
    void shouldNotPayProcessWithoutCVC() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCvcErrorHave();
    }

    @Test
    @DisplayName("42. Купить в кредит. Оплата не происходит. CVC/CVV не заполнено")
    void shouldNotCreditPayProcessWithoutCVC() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCvcErrorHave();
    }

    @Test
    @DisplayName("43. Купить. Оплата не происходит. CVC/CVV не три цифры")
    void shouldNotPayProcessWithoutCvcNot3Digits() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("42");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCvcErrorHave();
    }

    @Test
    @DisplayName("44. Купить в кредит. Оплата не происходит. CVC/CVV не три цифры")
    void shouldNotCreditPayProcessWithCvcNot3Digits() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("42");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCvcErrorHave();
    }

    @Test
    @DisplayName("45. Купить. Оплата происходит после исправления CVC/CVV")
    void shouldPayProcessWithCorrectedCvc() throws SQLException {
        PayPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("42");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCvcErrorHave();
        page.cleanCvcAndInputNewData(TestDataGenerator.generateCvc());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCvcErrorNotHave();
    }

    @Test
    @DisplayName("46. Купить в кредит. Оплата происходит после исправления CVC/CVV")
    void shouldCreditPayProcessWithCorrectedCvc() throws SQLException {
        CreditPayPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("42");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCvcErrorHave();
        page.cleanCvcAndInputNewData(TestDataGenerator.generateCvc());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(page.getDbTable()));
        page.checkCvcErrorNotHave();
    }
}