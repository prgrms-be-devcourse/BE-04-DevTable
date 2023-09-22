package com.mdh.owner.shop.application;

import com.mdh.owner.shop.application.dto.ShopDetailInfoResponse;
import com.mdh.owner.shop.infra.persistence.OwnerShopRepository;
import com.mdh.owner.shop.presentation.dto.OwnerShopCreateRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class OwnerShopService {

    private final OwnerShopRepository ownerShopRepository;

    @Transactional
    public Long createShop(Long ownerId, OwnerShopCreateRequest ownerShopCreateRequest) {
        return ownerShopRepository.save(ownerShopCreateRequest.toEntity(ownerId));
    }

    @Transactional(readOnly = true)
    public ShopDetailInfoResponse findShopByOwner(Long ownerId) {
        return ownerShopRepository.findShopById(ownerId)
                .map(ShopDetailInfoResponse::from)
                .orElseThrow(() -> new NoSuchElementException("해당 점주의 매장이 존재하지 않습니다.: " + ownerId));
    }
}