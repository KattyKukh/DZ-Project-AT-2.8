package ru.netology.steps;

import com.codeborne.selenide.Selenide;
import io.cucumber.java.ru.И;
import io.cucumber.java.ru.Когда;
import io.cucumber.java.ru.Пусть;
import io.cucumber.java.ru.Тогда;
import ru.netology.data.DataHelper;
import ru.netology.page.DashboardPage;
import ru.netology.page.LoginPage;
import ru.netology.page.VerificationPage;

import static com.codeborne.selenide.Selenide.$$;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class TemplateSteps {
    private static LoginPage loginPage;
    private static DashboardPage dashboardPage;
    private static VerificationPage verificationPage;


    @Пусть("открыта страница с формой авторизации {string}")
    public void openAuthPage(String url) {
        loginPage = Selenide.open(url, LoginPage.class);
    }

    @Пусть("пользователь залогинен с именем {string} и паролем {string}")
    public void logIn(String login, String password) {
        loginPage = Selenide.open("http://localhost:9999", LoginPage.class);
        verificationPage = loginPage.validLogin(login, password);
        dashboardPage = verificationPage.validVerify("12345");
        dashboardPage.verifyIsDashboardPage();
    }

    @Когда("пользователь пытается авторизоваться с именем {string} и паролем {string}")
    public void loginWithNameAndPassword(String login, String password) {
        verificationPage = loginPage.validLogin(login, password);
    }

    @Когда("пользователь переводит {int} рублей с карты с номером {string} на свою {int} карту с главной страницы")
    public void transferMoneyFromSecondToFirst(int transfer, String cardNumber, int numberItem) {
        Integer firstCardBalance = DashboardPage.getBalanceCard($$(".list__item div").get(numberItem - 1).getAttribute("data-test-id"));
        Integer secondCardBalance = DashboardPage.getBalanceCard($$(".list__item div").last().getAttribute("data-test-id"));
        String firstCardId = $$(".list__item div").get(numberItem - 1).getAttribute("data-test-id");
        String secondCardId = $$(".list__item div").last().getAttribute("data-test-id");
        if (secondCardBalance != 10_000) {
            int alignTransfer = (firstCardBalance + secondCardBalance) / 2 - secondCardBalance;
            DashboardPage.pressReplenishCard(secondCardId)
                    .replenishCardBalance(alignTransfer, DataHelper.CardInfo.getFirstCardNumber(firstCardId));
            firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
            secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        }
        DashboardPage.pressReplenishCard(firstCardId)
                .replenishCardBalance(transfer, cardNumber);
    }

    @И("пользователь вводит проверочный код 'из смс' {string}")
    public void setValidCode(String verificationCode) {
        dashboardPage = verificationPage.validVerify(verificationCode);
    }

    @Тогда("происходит успешная авторизация и пользователь попадает на страницу 'Личный кабинет'")
    public void verifyDashboardPage() {
        dashboardPage.verifyIsDashboardPage();
    }

    @Тогда("появляется ошибка о неверном коде проверки")
    public void verifyCodeIsInvalid() {
        verificationPage.verifyCodeIsInvalid();
    }

    @Тогда("баланс его {int} карты из списка на главной странице должен стать {int} рублей.")
    public void checkBalance(int numberItem, int expected) {
        int firstCardBalance = DashboardPage.getBalanceCard($$(".list__item div").get(numberItem - 1).getAttribute("data-test-id"));
        assertEquals(expected, firstCardBalance);
    }
}
