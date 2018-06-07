package ru.kpfu.itis.service;

import ru.kpfu.itis.model.BillingUser;

import java.util.List;

public interface TenantService {

    BillingUser getBillingUser(Long tenantId);

    List<BillingUser> getAllTenants();

    void save(BillingUser billingUser);

    boolean isAdmin(BillingUser billingUser);
}
