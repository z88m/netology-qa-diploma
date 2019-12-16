package ru.netology.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;
import ru.netology.testUtils.Card;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.*;


public class ProductPage {

    @Getter
    protected String host = "http://localhost:8080";
    @Getter
    protected SelenideElement header = $(By.cssSelector("h3"));
    @Getter
    protected SelenideElement payButton = $(byText("Купить")).parent().parent();
    @Getter
    protected SelenideElement creditButton = $(byText("Купить в кредит")).parent().parent();
    @Getter
    protected SelenideElement continueButton = $(byText("Продолжить")).parent().parent();
    @Getter
    protected SelenideElement cardNumberField = $(byText("Номер карты")).parent();
    @Getter
    protected SelenideElement monthField = $(byText("Месяц")).parent();
    @Getter
    protected SelenideElement yearField = $(byText("Год")).parent();
    @Getter
    protected SelenideElement ownerField = $(byText("Владелец")).parent();
    @Getter
    protected SelenideElement cvcField = $(byText("CVC/CVV")).parent();
    @Getter
    protected SelenideElement notificationApproved = $(".notification_status_ok ");
    @Getter
    protected SelenideElement notificationDeclined = $(".notification_status_error");

    public ProductPage() {
        open(host);
        header.shouldBe(Condition.visible);
    }

    public void inputData(Card card) {
        cardNumberField.$(".input__control").setValue(card.getNumber());
        monthField.$(".input__control").setValue(card.getMonth());
        yearField.$(".input__control").setValue(card.getYear());
        ownerField.$(".input__control").setValue(card.getCardholder());
        cvcField.$(".input__control").setValue(card.getCvc());
    }

    public PayPage payButtonClick() {
        PayPage payPage = page(PayPage.class);
        payPage.payButton.click();
        return payPage;
    }

    public CreditPayPage creditPayPage() {
        CreditPayPage creditPayPage = page(CreditPayPage.class);
        creditPayPage.creditButton.click();
        return creditPayPage;
    }

    public void continueButtonClick() {
        continueButton.click();
    }

}