package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.waiting.Waiting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {
}