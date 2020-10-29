package com.voltaire.delivery.repository;

import com.voltaire.delivery.model.DeliveryCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DeliveryCompanyRepository extends JpaRepository<DeliveryCompany, UUID> {
}
