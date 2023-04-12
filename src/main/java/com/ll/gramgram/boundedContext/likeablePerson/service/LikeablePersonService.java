package com.ll.gramgram.boundedContext.likeablePerson.service;

import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.repository.LikeablePersonRepository;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LikeablePersonService {
    private final LikeablePersonRepository likeablePersonRepository;
    private final InstaMemberService instaMemberService;
    private final MemberService memberService;

    @Transactional
    public RsData<LikeablePerson> like(Member member, String username, int attractiveTypeCode) {
        if (member.hasConnectedInstaMember() == false) {
            return RsData.of("F-2", "먼저 본인의 인스타그램 아이디를 입력해야 합니다.");
        }

        if (member.getInstaMember().getUsername().equals(username)) {
            return RsData.of("F-1", "본인을 호감상대로 등록할 수 없습니다.");
        }

        InstaMember fromInstaMember = member.getInstaMember();
        InstaMember toInstaMember = instaMemberService.findByUsernameOrCreate(username).getData();
        LikeablePerson likeablePerson = LikeablePerson
                .builder()
                .fromInstaMember(fromInstaMember)
                .fromInstaMemberUsername(member.getInstaMember().getUsername())
                .toInstaMember(toInstaMember)
                .toInstaMemberUsername(toInstaMember.getUsername())
                .attractiveTypeCode(attractiveTypeCode)
                .build();

        likeablePersonRepository.save(likeablePerson);

        fromInstaMember.addFromLikeablePerson(likeablePerson);
        toInstaMember.addToLikeablePerson(likeablePerson);

        return RsData.of("S-1", "입력하신 인스타유저(%s)를 호감상대로 등록되었습니다.".formatted(username), likeablePerson);
    }

    @Transactional
    public RsData<LikeablePerson> checkDuplicateOrUpdate(Member member, String username, int attractiveTypeCode) {
        LikeablePerson duplicateLikeablePerson = findLikeablePersonOne(member.getInstaMember(), username)
                .orElse(null);

        if (duplicateLikeablePerson == null) {
            return RsData.of("S-1", "데이터가 존재하지 않습니다.", duplicateLikeablePerson);
        }

        if (duplicateLikeablePerson.getAttractiveTypeCode() != attractiveTypeCode) {
            duplicateLikeablePerson.updateAttractiveTypeCode(attractiveTypeCode);
            return RsData.of("S-2", "매력 포인트를 업데이트하였습니다.", duplicateLikeablePerson);
        }

        return RsData.of("F-3", "이미 호감을 표시하였습니다.", duplicateLikeablePerson);
    }

    @Transactional
    public RsData delete(LikeablePerson likeablePerson) {
        likeablePersonRepository.delete(likeablePerson);

        String likeCanceledUsername = likeablePerson.getToInstaMember().getUsername();
        return RsData.of("S-1", "%s님에 대한 호감을 취소하였습니다.".formatted(likeCanceledUsername));
    }

    public RsData canActorDelete(Member actor, LikeablePerson likeablePerson) {
        if (likeablePerson == null) return RsData.of("F-1", "이미 삭제되었습니다.");

        // 수행자의 인스타계정 번호
        long actorInstaMemberId = actor.getInstaMember().getId();
        // 삭제 대상의 작성자(호감표시한 사람)의 인스타계정 번호
        long fromInstaMemberId = likeablePerson.getFromInstaMember().getId();

        if (actorInstaMemberId != fromInstaMemberId)
            return RsData.of("F-2", "권한이 없습니다.");

        return RsData.of("S-1", "삭제가능합니다.");
    }

    public RsData checkCountLessThanMax(Member member) {
        int count = getLikeablePeopleCount(member.getInstaMember().getId());
        return count >= AppConfig.getLikeablePersonFromMax() ?
                RsData.of("F-1", "호감 표시는 최대 10개까지 가능합니다.") :
                RsData.of("S-1", "");
    }

    public Optional<LikeablePerson> findLikeablePersonOne(InstaMember instaMember, String username) {
        return likeablePersonRepository
                .findByFromInstaMemberAndToInstaMember_Username(instaMember, username);
    }

    public Optional<LikeablePerson> findById(Long id) {
        return likeablePersonRepository.findById(id);
    }

    private List<LikeablePerson> findByFromInstaMemberId(Long fromInstaMemberId) {
        return likeablePersonRepository.findByFromInstaMemberId(fromInstaMemberId);
    }

    public int getLikeablePeopleCount(Long fromInstaMemberId) {
        return findByFromInstaMemberId(fromInstaMemberId).size();
    }


}
