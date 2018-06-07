package ru.kpfu.itis.service;

import ru.kpfu.itis.model.Plan;

public interface EmailService {

    void sendTrialPeriodStartMessage(String email);

    void sendPaymentSuccessMessage(String email, Plan plan, Long amount, String currency);

    void sendPaymentFailedAndNoCardMessage(String email, Plan plan);

    void sendPaymentFailedMessage(String email, Plan plan);
}
