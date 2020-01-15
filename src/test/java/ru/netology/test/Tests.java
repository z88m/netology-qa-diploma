package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.*;
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
    void shouldConfirmPayWithApprovedCard() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @Test
    @DisplayName("2. Купить в кредит. Успешная оплата кредитом с разрешённой картой")
    void shouldConfirmCreditPayWithApprovedCard() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @Test
    @DisplayName("3. Купить. Отказ в оплате запрещённой картой")
    void shouldNotConfirmPayWithDeclinedCard() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getDeclinedCard());
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @Test
    @DisplayName("4. Купить в кредит. Отказ в оплате кредитом с запрещённой картой")
    void shouldNotConfirmCreditPayWithDeclinedCard() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getDeclinedCard());
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @Test
    @DisplayName("5. Купить. Отказ. Номер карты не существует в системе")
    void shouldNotConfirmPayWithBadCard() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("4444444444444444");
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @Test
    @DisplayName("6. Купить в кредит. Отказ. Номер карты не существует в системе")
    void shouldNotConfirmCreditPayWithBadCard() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("4444444444444444");
        page.continueButtonClick();
        page.checkNotificationDeclinedVisible();
        assertEquals("DECLINED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @Test
    @DisplayName("7. Купить. Оплата не происходит. Незаполнен номер карты")
    void shouldNotPayProcessWithoutNumCard() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkCardNumberFormatErrorHave();
    }

    @Test
    @DisplayName("8. Купить. Оплата проходит после исправления номера карты на валидный")
    void shouldPayProcessAfterRetypeNumCard() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("444444444444444");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkCardNumberFormatErrorHave();
        page.cleanNumberAndInputNewData(TestDataGenerator.approvedCardNumber());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkCardNumberFormatErrorNotHave();

    }

    @Test
    @DisplayName("9. Купить в кредит. Оплата не происходит. Незаполнен номер карты")
    void shouldNotCreditPayProcessWithoutNumCard() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkCardNumberFormatErrorHave();
    }

    @Test
    @DisplayName("10. Купить в кредит. Оплата проходит после исправления номера карты на валидный")
    void shouldCreditPayProcessAfterRetypeNumCard() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanNumberAndInputNewData("444444444444444");
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
    @DisplayName("11. Купить. Оплата не происходит. Месяц введён одной цифрой")
    void shouldNotPayProcessWithShortMonth() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("1");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkMonthFormatErrorHave();
    }

    @Test
    @DisplayName("12. Купить. Оплата не происходит. Месяц 00")
    void shouldNotPayProcessWithMonth00() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("00");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("13. Купить. Оплата не происходит. Месяц 13")
    void shouldNotPayProcessWithMonth13() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("13");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("14. Купить в кредит. Оплата не происходит. Месяц введён одной цифрой")
    void shouldNotCreditPayProcessWithShortMonth() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("1");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkMonthFormatErrorHave();
    }

    @Test
    @DisplayName("15. Купить в кредит. Оплата не происходит. Месяц 00")
    void shouldNotCreditPayProcessWithMonth00() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("00");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("16. Купить в кредит. Оплата не происходит. Месяц 13")
    void shouldNotCreditPayProcessWithMonth13() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("13");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkMonthDateErrorHave();
    }

    @Test
    @DisplayName("17. Купить. Оплата не происходит. Год введён одной цифрой")
    void shouldNotPayProcessWithShortYear() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("1");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkYearFormatErrorHave();
    }

    @Test
    @DisplayName("18. Купить в кредит. Оплата не происходит. Год введён одной цифрой")
    void shouldNotCreditPayProcessWithShortYear() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("1");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkYearFormatErrorHave();
    }

    @Test
    @DisplayName("19. Купить. Оплата не происходит. Карта просрочена на месяц")
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
    @DisplayName("20. Купить в кредит. Оплата не происходит. Карта просрочена на месяц")
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
    @DisplayName("21. Купить. Оплата не происходит. Карта просрочена на год")
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
    @DisplayName("22. Купить в кредит. Оплата не происходит. Карта просрочена на год")
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
    @DisplayName("23. Купить. Оплата не происходит. Срок действия карты больше 5 лет")
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

    @Test
    @DisplayName("24. Купить в кредит. Оплата не происходит. Срок действия карты больше 5 лет")
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

    @Test
    @DisplayName("25. Купить. Оплата проходит после исправления месяца")
    void shouldPayProcessWithCorrectionWrongMonth() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("13");
        page.continueButtonClick();
        page.checkMonthDateErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.cleanMonthAndInputNewData("12");
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkMonthDateErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @Test
    @DisplayName("26. Купить в кредит. Оплата проходит после исправления месяца")
    void shouldCreditPayProcessWithCorrectionWrongMonth() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanMonthAndInputNewData("13");
        page.continueButtonClick();
        page.checkMonthDateErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.cleanMonthAndInputNewData("12");
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkMonthDateErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @Test
    @DisplayName("27. Купить. Оплата проходит после исправления года")
    void shouldPayProcessWithCorrectionWrongYear() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("1");
        page.continueButtonClick();
        page.checkYearFormatErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(2));
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkYearFormatErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
    }

    @Test
    @DisplayName("28. Купить в кредит. Оплата проходит после исправления года")
    void shouldCreditPayProcessWithCorrectionWrongYear() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanYearAndInputNewData("1");
        page.continueButtonClick();
        page.checkYearFormatErrorHave();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.cleanYearAndInputNewData(TestDataGenerator.getShiftedYYFromCurrent(2));
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        page.checkYearFormatErrorNotHave();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
    }

    @Test
    @DisplayName("29. Купить. Оплата не происходит. Имя владельца не заполнено")
    void shouldNotPayProcessWithoutCardholderName() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkOwnerErrorHave();
    }

    @Test
    @DisplayName("30. Купить в кредит. Оплата не происходит. Имя владельца не заполнено")
    void shouldNotCreditPayProcessWithoutCardholderName() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkOwnerErrorHave();
    }

    @Test
    @DisplayName("31. Купить. Оплата происходит после исправления имени владельца")
    void shouldPayProcessWithCorrectedCardholderName() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("Василий @№123");
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

    @Test
    @DisplayName("32. Купить в кредит. Купить. Оплата происходит после исправления имени владельца")
    void shouldCreditPayProcessWithCorrectedCardholderName() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanOwnerAndInputNewData("Василий @№123");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkOwnerFormatErrorHave();
        page.cleanOwnerAndInputNewData(TestDataGenerator.generateCardholder());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkOwnerFormatErrorNotHave();
    }

    @Test
    @DisplayName("33. Купить. Оплата не происходит. CVC/CVV не заполнено")
    void shouldNotPayProcessWithoutCVC() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkCvcErrorHave();
    }

    @Test
    @DisplayName("34. Купить в кредит. Оплата не происходит. CVC/CVV не заполнено")
    void shouldNotCreditPayProcessWithoutCVC() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkCvcErrorHave();
    }

    @Test
    @DisplayName("35. Купить. Оплата происходит после исправления CVC/CVV")
    void shouldPayProcessWithCorrectedCvc() {
        StartPage page = StartPage.payButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("42");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkCvcErrorHave();
        page.cleanCvcAndInputNewData(TestDataGenerator.generateCvc());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getPayTable()));
        page.checkCvcErrorNotHave();
    }

    @Test
    @DisplayName("36. Купить в кредит. Оплата происходит после исправления CVC/CVV")
    void shouldCreditPayProcessWithCorrectedCvc() {
        StartPage page = StartPage.creditPayButtonClick();
        page.inputData(TestDataGenerator.getApprovedCard());
        page.cleanCvcAndInputNewData("42");
        page.continueButtonClick();
        page.checkNotificationDeclinedNotVisible();
        page.checkNotificationApprovedNotVisible();
        assertEquals("", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkCvcErrorHave();
        page.cleanCvcAndInputNewData(TestDataGenerator.generateCvc());
        page.continueButtonClick();
        page.checkNotificationApprovedVisible();
        assertEquals("APPROVED", TestSQLHelper.getOperationStatus(TestSQLHelper.getCreditTable()));
        page.checkCvcErrorNotHave();
    }
}