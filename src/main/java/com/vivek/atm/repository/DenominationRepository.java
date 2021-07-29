package com.vivek.atm.repository;

import com.vivek.atm.model.DenominationEnum;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DenominationRepository extends JpaRepository<Denomination, DenominationEnum> {
}
