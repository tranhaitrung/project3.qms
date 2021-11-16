package com.hust.qms.repository;

import com.hust.qms.entity.VerifyCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VerifyCodeRepository extends JpaRepository<VerifyCode, Long> {
    List<VerifyCode> findAllByUserIdAndStatus(Long userId, String status);

    List<VerifyCode> findAllByUserIdAndStatusAndType(Long userId, String status, String type);

    List<VerifyCode> findAllByUsernameAndStatusAndType(String username, String status, String type);

    @Query(value = "SELECT * FROM verify_codes WHERE username = ?1 and status = ?2 and type =?3 and verify_code = ?4", nativeQuery = true)
    List<VerifyCode> findAllByUsernameAndStatusAndTypeAndVerifyCode(String username, String status, String type, String code);
}
