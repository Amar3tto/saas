package ru.kpfu.itis.model;

import ru.kpfu.itis.model.utils.PaymentStatus;

import javax.persistence.*;

@Entity
@Table(name = "saas_payment")
public class Payment {

    @Id
    private String id;

    private Long userId;

    private PaymentStatus status;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }
}
