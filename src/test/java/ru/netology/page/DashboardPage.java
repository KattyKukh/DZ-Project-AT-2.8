package ru.netology.page;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class DashboardPage {
    private final SelenideElement heading = $("[data-test-id=dashboard]");

    private static SelenideElement cardInfo;
    private static SelenideElement buttonReplenishCard;

    public void verifyIsDashboardPage() {
        heading.shouldBe(visible);
    }

    public static int getBalanceCard(String id) {
        cardInfo = $("[data-test-id='" + id + "']");
        int start = cardInfo.text().lastIndexOf(":");
        int finish = cardInfo.text().lastIndexOf("р.");
        String balanceText = cardInfo.text().substring(start + 1, finish).trim();
        return Integer.parseInt(balanceText);
    }

    public static TransferPage pressReplenishCard(String id) {
        cardInfo = $("[data-test-id='" + id + "']");
        buttonReplenishCard = cardInfo.find("[data-test-id=action-deposit]");
        buttonReplenishCard.click();
        return new TransferPage();
    }
}
