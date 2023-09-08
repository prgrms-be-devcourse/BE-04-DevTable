package com.mdh.devtable.waiting.infra.persistence;

import com.mdh.devtable.ownerwaiting.presentaion.dto.WaitingInfoResponseForOwner;
import com.mdh.devtable.waiting.domain.Waiting;
import com.mdh.devtable.waiting.domain.WaitingStatus;
import com.mdh.devtable.waiting.infra.persistence.dto.UserWaitingQueryDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface WaitingRepository extends JpaRepository<Waiting, Long> {

    @Query("select w from Waiting w where w.waitingStatus = 'PROGRESS' and w.userId = :userId")
    Optional<Waiting> findByProgressWaiting(@Param("userId") Long userId);

    @Query("""
             select new com.mdh.devtable.waiting.infra.persistence.dto.UserWaitingQueryDto(s.id, w.id, s.name, s.shopType, s.region.city, s.region.district, w.waitingNumber, w.waitingPeople.adultCount, w.waitingPeople.childCount)
             from Waiting w
             join Shop s on w.shopWaiting.shopId = s.id
             where w.userId = :userId and w.waitingStatus = :waitingStatus
            """)
    List<UserWaitingQueryDto> findAllByUserIdAndWaitingStatus(@Param("userId") Long userId, @Param("waitingStatus") WaitingStatus waitingStatus);

    @Query("""
            SELECT new com.mdh.devtable.ownerwaiting.presentaion.dto.WaitingInfoResponseForOwner(w.waitingNumber, u.email)
            FROM Waiting w
            JOIN User u ON w.userId = u.id
            JOIN Shop s ON w.shopWaiting.shopId = s.id
            WHERE s.userId = :ownerId AND
            w.waitingStatus = :waitingStatus
            """)
    List<WaitingInfoResponseForOwner> findWaitingByOwnerIdAndWaitingStatus(@Param("ownerId") Long ownerId, @Param("waitingStatus") WaitingStatus waitingStatus);
}