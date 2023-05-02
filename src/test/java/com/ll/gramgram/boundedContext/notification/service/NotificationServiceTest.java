package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.entity.TypeCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class NotificationServiceTest {
    @Autowired
    private NotificationService notificationService;

    @Autowired
    private LikeablePersonService likeablePersonService;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("알림 whenAfterLike 기본 저장 테스트")
    void t001() {
        //given
        String fromInstaMemberUsername = "insta_user3";
        String toInstaMemberUsername = "insta_user4";

        LikeablePerson likeablePerson = likeablePersonService
                .findByFromInstaMember_usernameAndToInstaMember_username(fromInstaMemberUsername, toInstaMemberUsername)
                .orElseThrow(() -> new RuntimeException("데이터를 찾을 수 없습니다."));

        //when
        notificationService.whenAfterLike(likeablePerson);

        //then
        List<Notification> notifications = notificationService.findByToInstaMember(likeablePerson.getToInstaMember());
        assertTrue(notifications.size() > 0);
        assertEquals(TypeCode.Like, notifications.get(0).getTypeCode());
    }

    @Test
    @DisplayName("좋아요 기능 사용 시 Notification 저장 테스트")
    void t002() {
        //given
        String memberName = "user2";
        String instaUserName = "insta_user3";
        Member member1 = memberService.findByUsername(memberName)
                .orElseThrow(() -> new RuntimeException("유저데이터가 없습니다. base/initDate/NotProd.java 를 확인해보세요."));

        //when
        likeablePersonService.like(member1, instaUserName, 1);

        //then
        List<Notification> notifications = notificationService.findAll();

        Optional<Notification> opNotification = notifications.stream()
                .filter(n -> n.getFromInstaMember().equals(member1.getInstaMember()) &&
                        n.getToInstaMember().getUsername().equals(instaUserName))
                .findFirst();

        assertTrue(opNotification.isPresent());

        Notification notification = opNotification.get();
        assertEquals(TypeCode.Like, notification.getTypeCode());
    }
}