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

import java.time.Duration;
import java.time.LocalDateTime;
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
    @DisplayName("whenAfterLike 기본 저장 테스트")
    void t001() {
        //given
        String fromInstaMemberUsername = "insta_user3";
        String toInstaMemberUsername = "insta_user4";

        LikeablePerson likeablePerson = likeablePersonService
                .findByFromInstaMember_usernameAndToInstaMember_username(fromInstaMemberUsername, toInstaMemberUsername)
                .orElseThrow(() -> new RuntimeException("데이터를 찾을 수 없습니다."));

        //when
        notificationService.makeLike(likeablePerson);

        //then
        List<Notification> notifications = notificationService.findByToInstaMember(likeablePerson.getToInstaMember());
        assertTrue(notifications.size() > 0);
        assertEquals(TypeCode.LIKE, notifications.get(0).getTypeCode());
    }

    @Test
    @DisplayName("like 기능 사용 시 Notification 저장 테스트")
    void t002() {
        //given
        String memberName = "user2";
        String instaUserName = "insta_user3";
        Member member = memberService.findByUsername(memberName)
                .orElseThrow(() -> new RuntimeException("유저데이터가 없습니다. base/initDate/NotProd.java 를 확인해보세요."));

        //when
        likeablePersonService.like(member, instaUserName, 1);

        //then
        List<Notification> notifications = notificationService.findAll();

        Optional<Notification> opNotification = notifications.stream()
                .filter(n -> n.getFromInstaMember().equals(member.getInstaMember()) &&
                        n.getToInstaMember().getUsername().equals(instaUserName))
                .findFirst();

        assertTrue(opNotification.isPresent());

        Notification notification = opNotification.get();
        assertEquals(TypeCode.LIKE, notification.getTypeCode());
    }

    @Test
    @DisplayName("호감 사유 변경 시 Notification 저장 테스트")
    void t003() {
        //given
        String memberName = "user3";
        Member member = memberService.findByUsername(memberName)
                .orElseThrow(() -> new RuntimeException("데이터가 존재하지 않습니다. /base/initDate/NotProd.java 를 확인해주세요"));

        String instaUserName = "insta_user4";
        LikeablePerson likeablePerson = likeablePersonService
                .findByFromInstaMember_usernameAndToInstaMember_username(member.getInstaMember().getUsername(), instaUserName)
                .orElseThrow(() -> new RuntimeException("데이터가 존재하지 않습니다. /base/initDate/NotProd.java 를 확인해주세요"));

        //when
        int oldAttractiveTypeCode = likeablePerson.getAttractiveTypeCode();
        int newAttractiveTypeCode = 2;
        likeablePersonService.modifyAttractive(member, likeablePerson, newAttractiveTypeCode);

        //then
        List<Notification> notifications = notificationService.findAll();
        Optional<Notification> opNotification = notifications.stream()
                .filter(n -> n.getFromInstaMember().equals(likeablePerson.getFromInstaMember()) &&
                        n.getToInstaMember().equals(likeablePerson.getToInstaMember()) &&
                        n.getTypeCode().equals(TypeCode.MODIFY_ATTRACTIVE_TYPE))
                .findFirst();

        assertTrue(opNotification.isPresent());
        Notification notification = opNotification.get();

        assertEquals(oldAttractiveTypeCode, notification.getOldAttractiveTypeCode());
        assertEquals(newAttractiveTypeCode, notification.getNewAttractiveTypeCode());
    }

    @Test
    @DisplayName("시간차이 테스트")
    void t004() {
        LocalDateTime dateTime = LocalDateTime.of(2023, 5, 1, 12, 0, 0);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(dateTime, now);
        System.out.println("시간 차이: " + duration.toDays() + "일" + duration.toHours() % 24 + "시," + duration.toMinutes() % 60 + "분");
    }
}