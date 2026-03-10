package com.auth.letter.repository;

import com.auth.letter.entity.AuthLetter;
import com.auth.letter.enums.AuthLetterStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuthLetterRepository extends JpaRepository<AuthLetter, Long> {

    List<AuthLetter> findByStatus(AuthLetterStatus status);

    List<AuthLetter> findByStatusIn(List<AuthLetterStatus> statuses);

    @Query("SELECT a FROM AuthLetter a WHERE a.status = 'PUBLISHED' AND a.validTo < :now")
    List<AuthLetter> findExpiredLetters(LocalDateTime now);

    @Query("SELECT a FROM AuthLetter a WHERE a.status = 'PUBLISHED' AND a.validFrom <= :now AND a.validTo >= :now")
    List<AuthLetter> findCurrentlyValid(LocalDateTime now);
}