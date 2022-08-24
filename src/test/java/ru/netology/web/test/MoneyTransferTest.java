package ru.netology.web.test;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.CsvSource;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.*;

import static com.codeborne.selenide.Selenide.open;

class MoneyTransferTest {

    private DataHelper.AuthInfo authInfo = DataHelper.getAuthInfo();
    private Integer firstCardBalance;
    private Integer secondCardBalance;
    private String firstCardId = DataHelper.CardInfo.getFirstCardInfo(authInfo).getId();
    private String secondCardId = DataHelper.CardInfo.getSecondCardInfo(authInfo).getId();

    @BeforeEach
    void setup() {
        open("http://localhost:9999");
    }

    void logIn() {
        var loginPage = open("http://localhost:9999", LoginPageV2.class);
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
    }

    @Test
    void shouldLogIn() {
        var loginPage = open("http://localhost:9999", LoginPageV2.class);
        var authInfo = DataHelper.getAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        var verificationCode = DataHelper.getVerificationCodeFor(authInfo);
        verificationPage.validVerify(verificationCode);
        $("[data-test-id=dashboard]").shouldBe(visible);
        $("[data-test-id=dashboard] ~h1").shouldBe(visible).shouldHave(text("Ваши карты"));
    }

    @Test
    void shouldNotLogInIfUsersDataIsWrong() {
        var loginPage = open("http://localhost:9999", LoginPageV2.class);
        var authInfo = DataHelper.getWrongAuthInfo();
        var verificationPage = loginPage.validLogin(authInfo);
        $("[data-test-id=error-notification]").shouldBe(visible).shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    @ParameterizedTest(name = "{index} {0}")
    @CsvSource({"Transfer = 1,1",
            "Transfer = 20,20",
            "Transfer = 300,300",
            "Transfer = 4000,4000"})
    void shouldTransferMoneyFromFirstCardToSecond(String testName, int transfer) {
        logIn();
        firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
        secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        if (firstCardBalance < transfer) {
            int alignTransfer = (firstCardBalance + secondCardBalance) / 2 - firstCardBalance;
            DashboardPage.pressReplenishCard(firstCardId)
                    .replenishCardBalance(alignTransfer, DataHelper.CardInfo.getSecondCardInfo(authInfo));
            firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
            secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        }

        DashboardPage.pressReplenishCard(secondCardId)
                .replenishCardBalance(transfer, DataHelper.CardInfo.getFirstCardInfo(authInfo));
        assertEquals(firstCardBalance - transfer, DashboardPage.getBalanceCard(firstCardId));
        assertEquals(secondCardBalance + transfer, DashboardPage.getBalanceCard(secondCardId));
    }

    @ParameterizedTest(name = "{index} {0}")
    @CsvSource({"Transfer = 300,300",
            "Transfer = 4000,4000"})
    void shouldNotTransferMoneyFromFirstCardToSecondIfCancelLed(String testName, int transfer) {
        logIn();
        firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
        secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        if (firstCardBalance < transfer) {
            int alignTransfer = (firstCardBalance + secondCardBalance) / 2 - firstCardBalance;
            DashboardPage.pressReplenishCard(firstCardId)
                    .replenishCardBalance(alignTransfer, DataHelper.CardInfo.getSecondCardInfo(authInfo));
            firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
            secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        }

        DashboardPage.pressReplenishCard(secondCardId)
                .replenishCardCancel(transfer, DataHelper.CardInfo.getFirstCardInfo(authInfo));
        assertEquals(firstCardBalance, DashboardPage.getBalanceCard(firstCardId));
        assertEquals(secondCardBalance, DashboardPage.getBalanceCard(secondCardId));
    }

    @ParameterizedTest(name = "{index} {0}")
    @CsvSource({"Transfer = 4,4",
            "Transfer = 30,30",
            "Transfer = 200,200",
            "Transfer = 1000,1000"})
    void shouldTransferMoneyFromSecondCardToFirst(String testName, int transfer) {
        logIn();
        firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
        secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        if (secondCardBalance < transfer) {
            int alignTransfer = (firstCardBalance + secondCardBalance) / 2 - secondCardBalance;
            DashboardPage.pressReplenishCard(secondCardId)
                    .replenishCardBalance(alignTransfer, DataHelper.CardInfo.getFirstCardInfo(authInfo));
            firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
            secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        }
        DashboardPage.pressReplenishCard(firstCardId)
                .replenishCardBalance(transfer, DataHelper.CardInfo.getSecondCardInfo(authInfo));
        assertEquals(firstCardBalance + transfer, DashboardPage.getBalanceCard(firstCardId));
        assertEquals(secondCardBalance - transfer, DashboardPage.getBalanceCard(secondCardId));
    }

    @ParameterizedTest(name = "{index} {0}")
    @CsvSource({"Transfer = 200,200",
            "Transfer = 1000,1000"})
    void shouldNotTransferMoneyFromSecondCardToFirstIfCancelled(String testName, int transfer) {
        logIn();
        firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
        secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        if (secondCardBalance < transfer) {
            int alignTransfer = (firstCardBalance + secondCardBalance) / 2 - secondCardBalance;
            DashboardPage.pressReplenishCard(secondCardId)
                    .replenishCardBalance(alignTransfer, DataHelper.CardInfo.getFirstCardInfo(authInfo));
            firstCardBalance = DashboardPage.getBalanceCard(firstCardId);
            secondCardBalance = DashboardPage.getBalanceCard(secondCardId);
        }
        DashboardPage.pressReplenishCard(firstCardId)
                .replenishCardCancel(transfer, DataHelper.CardInfo.getSecondCardInfo(authInfo));
        assertEquals(firstCardBalance + transfer, DashboardPage.getBalanceCard(firstCardId));
        assertEquals(secondCardBalance, DashboardPage.getBalanceCard(secondCardId));
    }
}

