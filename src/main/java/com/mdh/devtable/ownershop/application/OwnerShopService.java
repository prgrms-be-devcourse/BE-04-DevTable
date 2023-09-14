package com.mdh.devtable.ownershop.application;

import com.mdh.devtable.ownershop.infra.persistence.OwnerShopRepository;
import com.mdh.devtable.ownershop.presentation.dto.OwnerShopCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OwnerShopService {

    private final OwnerShopRepository ownerShopRepository;

    @Transactional
    public Long createShop(Long ownerId, OwnerShopCreateRequest ownerShopCreateRequest) {
        return ownerShopRepository.save(ownerShopCreateRequest.toEntity(ownerId));
    }
}