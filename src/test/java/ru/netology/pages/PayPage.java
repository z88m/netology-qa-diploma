package ru.netology.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ru.netology.testUtils.Cards;

@Getter
public class PayPage extends ProductPage {

    public String dbTable="app.payment_entity";

    public void checkOperationApproved() {
        notificationApproved.waitUntil(Condition.visible, 15000);
        notificationDeclined.waitUntil(Condition.not(Condition.visible), 10000);
    }

    public void checkOperationDeclined() {
        notificationDeclined.waitUntil(Condition.visible, 15000);
        notificationApproved.waitUntil(Condition.not(Condition.visible), 10000);
    }

    public static PayPage selectPayPage(Cards card) {
        ProductPage productPage = new ProductPage();
        PayPage payPage = productPage.payButtonClick();
        payPage.inputData(card);
        payPage.continueButtonClick();
        return payPage;
    }
}