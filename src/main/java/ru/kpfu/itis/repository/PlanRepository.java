package ru.kpfu.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.model.Plan;

import java.util.List;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Plan findByCode(String code);

    @Modifying
    void deleteByCodeNotIn(List<String> codes);

    @Modifying
    void deleteByCode(String code);
}
