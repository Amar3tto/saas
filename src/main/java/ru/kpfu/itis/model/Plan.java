package ru.kpfu.itis.model;

import javax.persistence.*;

@Entity
@Table(name = "saas_plans", indexes = {
        @Index(name = "plan_code_index", columnList = Plan.CODE, unique = true)
})
public class Plan {

    public static final String CODE = "code";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    @Column(name = CODE)
    private String code;

    private Long price;

    @Column(name = "period_in_days")
    private Long periodInDays;

    @Column(name = "is_tariff_open")
    private Boolean isTariffOpen;

    private String interval;

    @Column(name = "interval_count")
    private Integer intervalCount;

    private Boolean trial;

    public static String getCODE() {
        return CODE;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getPeriodInDays() {
        return periodInDays;
    }

    public void setPeriodInDays(Long periodInDays) {
        this.periodInDays = periodInDays;
    }

    public Boolean getTariffOpen() {
        return isTariffOpen;
    }

    public void setTariffOpen(Boolean tariffOpen) {
        isTariffOpen = tariffOpen;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public Integer getIntervalCount() {
        return intervalCount;
    }

    public void setIntervalCount(Integer intervalCount) {
        this.intervalCount = intervalCount;
    }

    public Boolean getTrial() {
        return trial;
    }

    public void setTrial(Boolean trial) {
        this.trial = trial;
    }
}
