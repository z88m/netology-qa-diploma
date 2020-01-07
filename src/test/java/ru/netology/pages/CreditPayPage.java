package ru.netology.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;

public class CreditPayPage extends StartPage {

    @Getter
    public String dbTable = "app.credit_request_entity";

}