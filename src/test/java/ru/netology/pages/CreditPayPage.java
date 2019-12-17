package ru.netology.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;
import ru.netology.testUtils.Cards;


@Getter
public class CreditPayPage extends ProductPage {

    public String dbTable="app.credit_request_entity";

    public void checkOperationApproved() {
        notificationApproved.waitUntil(Condition.visible, 15000);
        notificationDeclined.waitUntil(Condition.not(Condition.visible), 10000);
    }

    public void checkOperationDeclined() {
        notificationDeclined.waitUntil(Condition.visible, 15000);
        notificationApproved.waitUntil(Condition.not(Condition.visible), 10000);
    }

    public static CreditPayPage selectCreditPayPage(Cards card) {
        ProductPage productPage = new ProductPage();
        CreditPayPage creditPayPage = productPage.creditPayButtonClick();
        creditPayPage.inputData(card);
        creditPayPage.continueButtonClick();
        return creditPayPage;
    }
}