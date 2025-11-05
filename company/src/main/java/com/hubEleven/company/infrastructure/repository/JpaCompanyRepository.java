package com.hubEleven.company.infrastructure.repository;

import com.hubEleven.company.domain.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface JpaCompanyRepository extends JpaRepository<Company, UUID> {
    boolean existsByHubIdAndNameIgnoreCase(UUID hubId, String companyName);
}
