package com.scalable.verification;

import com.scalable.verification.entity.Kyc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KycRepository extends JpaRepository<Kyc, Long> {
    Kyc findByLoanId(Long loanId);
}
