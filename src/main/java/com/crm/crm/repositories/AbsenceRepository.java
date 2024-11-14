package com.crm.crm.repositories;

import com.crm.crm.entities.Absence;
import com.crm.crm.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface AbsenceRepository extends JpaRepository<Absence, Long> {
    List<Absence> findByUserAndStartDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<Absence> findByUserAndEndDateGreaterThanEqualAndStartDateLessThanEqual(User user, LocalDate startDate, LocalDate endDate);
}
