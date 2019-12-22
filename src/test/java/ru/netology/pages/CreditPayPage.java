package ru.netology.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;

public class CreditPayPage extends StartPage {

    @Getter
    public String dbTable = "app.credit_request_entity";

    public void checkNotificationApprovedVisible() {
        notificationApproved.waitUntil(Condition.visible, 15000);
    }

    public void checkNotificationDeclinedNotVisible() {
        notificationDeclined.waitUntil(Condition.not(Condition.visible), 15000);
    }

    public void checkNotificationDeclinedVisible() {
        notificationDeclined.waitUntil(Condition.visible, 15000);
    }

    public void checkNotificationApprovedNotVisible() {
        notificationApproved.waitUntil(Condition.not(Condition.visible), 15000);
    }
}