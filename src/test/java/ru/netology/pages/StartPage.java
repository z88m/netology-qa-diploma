package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.By;
import ru.netology.testUtils.Cards;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;


public class StartPage {

    protected String host = "http://localhost:8080";
    protected SelenideElement header = $(By.cssSelector("h3"));
    protected SelenideElement payButton = $(byText("Купить")).parent().parent();
    protected SelenideElement creditButton = $(byText("Купить в кредит")).parent().parent();
    protected SelenideElement continueButton = $(byText("Продолжить")).parent().parent();
    protected SelenideElement cardNumberField = $(byText("Номер карты")).parent();
    protected SelenideElement monthField = $(byText("Месяц")).parent();
    protected SelenideElement yearField = $(byText("Год")).parent();
    protected SelenideElement ownerField = $(byText("Владелец")).parent();
    protected SelenideElement cvcField = $(byText("CVC/CVV")).parent();
    protected SelenideElement notificationApproved = $(".notification_status_ok ");
    protected SelenideElement notificationDeclined = $(".notification_status_error");

    public StartPage() {
        open(host);
        header.shouldBe(Condition.visible);
    }

    public void inputData(Cards card) {
        cardNumberField.$(".input__control").setValue(card.getNumber());
        monthField.$(".input__control").setValue(card.getMonth());
        yearField.$(".input__control").setValue(card.getYear());
        ownerField.$(".input__control").setValue(card.getCardholder());
        cvcField.$(".input__control").setValue(card.getCvc());
    }

    public static PayPage payButtonClick() {
        PayPage payPage = page(PayPage.class);
        payPage.payButton.click();
        return payPage;
    }

    public static CreditPayPage creditPayButtonClick() {
        CreditPayPage creditPayPage = page(CreditPayPage.class);
        creditPayPage.creditButton.click();
        return creditPayPage;
    }

    public void continueButtonClick() {
        continueButton.click();
    }

    /*public PayPage selectPayPage(Cards card) {
        StartPage startPage = new StartPage();
        PayPage payPage = startPage.payButtonClick();
        payPage.inputData(card);
        payPage.continueButtonClick();
        return payPage;
    }

    public CreditPayPage selectCreditPayPage(Cards card) {
        StartPage startPage = new StartPage();
        CreditPayPage creditPayPage = startPage.creditPayButtonClick();
        creditPayPage.inputData(card);
        creditPayPage.continueButtonClick();
        return creditPayPage;
    }*/

}