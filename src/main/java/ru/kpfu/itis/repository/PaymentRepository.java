package ru.kpfu.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.kpfu.itis.model.Payment;

public interface PaymentRepository extends JpaRepository<Payment, String> {
}
