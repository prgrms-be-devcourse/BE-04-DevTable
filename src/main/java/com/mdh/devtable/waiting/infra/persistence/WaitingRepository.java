package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.waiting.domain.Waiting;
import com.mdh.devtable.waiting.infra.persistence.dto.WaitingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @Query("select w from Waiting w where w.waitingStatus = 'PROGRESS' and w.userId = :userId")
    Optional<Waiting> findByProgressWaiting(@Param("userId") Long userId);

    @Query("""
            select
                new com.mdh.devtable.waiting.infra.persistence.dto.WaitingDetails
                (
                    s.name, s.shopType, s.region.district, s.shopDetails.phoneNumber, 
                    w.waitingNumber, w.waitingStatus, w.waitingPeople.adultCount, w.waitingPeople.childCount, 
                    w.createdDate, w.modifiedDate
                )
            from Waiting w
            join Shop s on w.shopWaiting.shopId = s.id
            where w.id = :waitingId
            """)
    WaitingDetails findByWaitingDetails(@Param("waitingId") Long waitingId);
}