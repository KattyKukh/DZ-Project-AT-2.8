package ru.netology.data;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
// Данный класс как пример генерации тестовых данных
// Вместо передачи данных через сценарий (feature)
// можно вызывать подобные методы непосредственно в шагах сценария (steps)
public class DataHelper {

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    public static AuthInfo getWrongAuthInfo() {
        return new AuthInfo("sonya", "123qwerty");
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        return new VerificationCode("12345");
    }

    @Value
    @AllArgsConstructor
    public static class CardInfo {
        private String id;
        private String cardNumber;

        public static String getFirstCardNumber(String id) {
            return "5559 0000 0000 0001";
        }

        public static String getSecondCardNumber(String id) {
            return "5559 0000 0000 0002";
        }
    }
}
