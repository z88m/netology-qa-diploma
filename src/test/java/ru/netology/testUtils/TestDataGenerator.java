package ru.netology.testUtils;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.util.Random;

public class TestDataGenerator {

    public static String approvedCardNumber() {
        return "4444444444444441";
    }

    public static String declinedCardNumber() {
        return "4444444444444442";
    }

    public static String month() {
        Random random = new Random();
        return String.format("%02d", (random.nextInt(12) + 1));
    }

    public static String currentYearPlusOrMinus(int years) {
        LocalDate date = LocalDate.now().plusDays(years);
        return String.format("%02d", date.getYear());
    }

    public static String cardholder() {
        Faker faker = new Faker();
        return faker.name().fullName();
    }

    public static String cvc() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}