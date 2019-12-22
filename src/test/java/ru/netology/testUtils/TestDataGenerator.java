package ru.netology.testUtils;

import com.github.javafaker.Faker;
import lombok.val;

import java.time.LocalDate;
import java.util.Random;

public class TestDataGenerator {

    public static String approvedCardNumber() {
        return "4444444444444441";
    }

    public static String declinedCardNumber() {
        return "4444444444444442";
    }

    public static Cards getApprovedCard() {
        Cards approvedCard = new Cards();
        approvedCard.setNumber(approvedCardNumber());
        approvedCard.setMonth(generateMonth());
        approvedCard.setYear(getShiftedYearFromCurrent(2));
        approvedCard.setCardholder(generateCardholder());
        approvedCard.setCvc(generateCvc());
        return approvedCard;
    }

    public static Cards getDeclinedCard() {
        Cards declinedCard = new Cards();
        declinedCard.setNumber(declinedCardNumber());
        declinedCard.setMonth(generateMonth());
        declinedCard.setYear(getShiftedYearFromCurrent(2));
        declinedCard.setCardholder(generateCardholder());
        declinedCard.setCvc(generateCvc());
        return declinedCard;
    }

    public static String generateMonth() {
        Random random = new Random();
        return String.format("%02d", (random.nextInt(12) + 1));
    }

    public static String getShiftedYearFromCurrent(int shiftingYear) {
        LocalDate date = LocalDate.now().plusDays(shiftingYear);
        return String.format("%02d", date.getYear());
    }

    public static String generateCardholder() {
        Faker faker = new Faker();
        return faker.name().fullName();
    }

    public static String generateCvc() {
        Random random = new Random();
        return String.format("%03d", random.nextInt(1000));
    }
}