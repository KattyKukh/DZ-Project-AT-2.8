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
        Integer firstCardBalance = dashboardPage.getBalanceCard(numberItem - 1);
        Integer secondCardBalance = dashboardPage.getBalanceCard(1);
        if (secondCardBalance < 10_000) {
            int alignTransfer = (firstCardBalance + secondCardBalance) / 2 - secondCardBalance;
            dashboardPage.pressReplenishCard(1)
                    .replenishCardBalance(alignTransfer, DataHelper.getFirstCardNumber());
            firstCardBalance = dashboardPage.getBalanceCard(numberItem - 1);
            secondCardBalance = dashboardPage.getBalanceCard(1);
        }
        if (secondCardBalance > 10_000) {
            int alignTransfer = (firstCardBalance + secondCardBalance) / 2 - firstCardBalance;
            dashboardPage.pressReplenishCard(numberItem - 1)
                    .replenishCardBalance(alignTransfer, cardNumber);
            firstCardBalance = dashboardPage.getBalanceCard(numberItem - 1);
            secondCardBalance = dashboardPage.getBalanceCard(1);
        }
        dashboardPage.pressReplenishCard(numberItem - 1)
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
        int firstCardBalance = dashboardPage.getBalanceCard(numberItem - 1);
        assertEquals(expected, firstCardBalance);
    }
}
