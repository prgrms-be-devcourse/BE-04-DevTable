package com.mdh.owner.shop.application;

import com.mdh.owner.shop.application.dto.ShopDetailInfoResponse;
import com.mdh.owner.shop.infra.persistence.OwnerShopRepository;
import com.mdh.owner.shop.presentation.dto.OwnerShopCreateRequest;
import com.mdh.owner.shop.presentation.dto.OwnerShopUpdateRequest;
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
        return ownerShopRepository.findShopByOwnerId(ownerId)
                .map(ShopDetailInfoResponse::from)
                .orElseThrow(() -> new NoSuchElementException("해당 점주의 매장이 존재하지 않습니다.: " + ownerId));
    }

    @Transactional
    public void updateShop(Long shopId, OwnerShopUpdateRequest ownerShopUpdateRequest) {
        var shop = ownerShopRepository.findShopById(shopId)
                .orElseThrow(() -> new NoSuchElementException("해당 점주의 매장이 존재하지 않습니다.: " + shopId));

        var newShopDetails = ownerShopUpdateRequest.shopDetailsRequest().toVO();
        var newShopAddress = ownerShopUpdateRequest.shopAddressRequest().toVO();
        var newRegion = ownerShopUpdateRequest.regionRequest().toEntity();

        shop.update(
                ownerShopUpdateRequest.name(),
                ownerShopUpdateRequest.description(),
                ownerShopUpdateRequest.shopType(),
                newShopDetails,
                newShopAddress,
                newRegion
        );
    }

}