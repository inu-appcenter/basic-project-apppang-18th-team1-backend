package com.team1.appang.service.address;

import com.team1.appang.dto.address.AddressCreateRequest;
import com.team1.appang.dto.address.AddressData;
import com.team1.appang.dto.address.AddressUpdateRequest;
import com.team1.appang.entity.Address;
import com.team1.appang.entity.Member;
import com.team1.appang.exception.AddressAccessDeniedException;
import com.team1.appang.exception.AddressNotFoundException;
import com.team1.appang.exception.MemberNotFoundException;
import com.team1.appang.repository.AddressRepository;
import com.team1.appang.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final MemberRepository memberRepository;

    //배송지 등록
    @Transactional
    public AddressData createAddress(Long memberId, AddressCreateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        //기본 배송지로 등록하려는 경우, 기존 기본 배송지가 있다면 먼저 해제
        if (request.isDefault()) {
            addressRepository.findByMemberIdAndIsDefaultTrue(memberId)
                    .ifPresent(existing -> existing.changeDefault(false));
        }

        Address address = Address.builder()
                .member(member)
                .alias(request.alias())
                .recipientName(request.recipientName())
                .recipientPhone(request.recipientPhone())
                .mainAddress(request.mainAddress())
                .detailAddress(request.detailAddress())
                .deliveryMessage(request.deliveryMessage())
                .isDefault(request.isDefault())
                .build();
        addressRepository.save(address);

        return AddressData.from(address);
    }

    //내 배송지 목록 조회. 기본 배송지가 먼저, 그 다음 최신순
    @Transactional(readOnly = true)
    public List<AddressData> getAddresses(Long memberId) {
        return addressRepository.findByMemberIdOrderByIsDefaultDescCreatedAtDesc(memberId).stream()
                .map(AddressData::from)
                .toList();
    }

    //배송지 수정
    @Transactional
    public AddressData updateAddress(Long memberId, Long addressId, AddressUpdateRequest request) {
        Address address = findOwnedAddress(memberId, addressId);
        address.update(
                request.alias(),
                request.recipientName(),
                request.recipientPhone(),
                request.mainAddress(),
                request.detailAddress(),
                request.deliveryMessage()
        );
        return AddressData.from(address);
    }

    //배송지 삭제. 기본 배송지도 삭제 가능(삭제 후 기본 배송지가 없는 상태를 허용)
    @Transactional
    public Long deleteAddress(Long memberId, Long addressId) {
        Address address = findOwnedAddress(memberId, addressId);
        addressRepository.delete(address);
        return addressId;
    }

    //기본 배송지 지정. 기존에 기본이던 배송지가 있다면 해제 후 새로 지정
    @Transactional
    public AddressData setDefaultAddress(Long memberId, Long addressId) {
        Address address = findOwnedAddress(memberId, addressId);

        addressRepository.findByMemberIdAndIsDefaultTrue(memberId)
                .filter(existing -> !existing.getId().equals(addressId))
                .ifPresent(existing -> existing.changeDefault(false));

        address.changeDefault(true);
        return AddressData.from(address);
    }

    //배송지를 조회하고 본인 소유인지 검증
    private Address findOwnedAddress(Long memberId, Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(AddressNotFoundException::new);

        if (!address.getMember().getId().equals(memberId)) {
            throw new AddressAccessDeniedException();
        }
        return address;
    }
}
