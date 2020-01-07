package ru.netology.pages;

import com.codeborne.selenide.Condition;
import lombok.Getter;

public class PayPage extends StartPage {

    @Getter
    public String dbTable = "app.payment_entity";

}