package com.hubEleven.company.domain.repository;

import com.hubEleven.company.domain.model.Company;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface CompanyRepository {
    Company save(Company company);
    Optional<Company> findById(UUID companyId);
    boolean existsByHubIdAndName(UUID hubId, String companyName);
    Page<Company> search(CompanySearchCondition condition, Pageable pageable);
}
