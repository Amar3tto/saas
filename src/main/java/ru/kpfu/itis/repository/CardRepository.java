package ru.kpfu.itis.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kpfu.itis.model.Card;

@Repository
public interface CardRepository extends JpaRepository<Card, String> {

}
