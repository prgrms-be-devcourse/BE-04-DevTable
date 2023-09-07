package com.mdh.devtable.waiting.application;

import com.mdh.devtable.waiting.infra.persistence.WaitingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Component
public class WaitingServiceValidator {

    private final WaitingRepository waitingRepository;

    @Transactional(readOnly = true)
    public boolean isExistsWaiting(Long userId) {
        return waitingRepository.findByProgressWaiting(userId)
                .isPresent();
    }
}
