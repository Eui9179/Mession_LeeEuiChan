package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.entity.TypeCode;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> findByToInstaMember(InstaMember toInstaMember) {
        return notificationRepository.findByToInstaMember(toInstaMember);
    }
        /**
         *     private LocalDateTime readDate;
         *     @ManyToOne
         *     @ToString.Exclude
         *     private InstaMember toInstaMember; // 메세지 받는 사람(호감 받는 사람)
         *     @ManyToOne
         *     @ToString.Exclude
         *     private InstaMember fromInstaMember; // 메세지를 발생시킨 행위를 한 사람(호감표시한 사람)
         *     private String typeCode; // 호감표시=Like, 호감사유변경=ModifyAttractiveType
         *     private String oldGender; // 해당사항 없으면 null
         *     private int oldAttractiveTypeCode; // 해당사항 없으면 0
         *     private String newGender; // 해당사항 없으면 null
         *     private int newAttractiveTypeCode; // 해당사항 없으면 0
         */

    @Transactional
    public Notification whenAfterLike(LikeablePerson likeablePerson) {
        Notification notification = Notification.builder()
                .toInstaMember(likeablePerson.getToInstaMember())
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .typeCode(TypeCode.Like)
                .build();
        return notificationRepository.save(notification);
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }
}
        /**
         * to, from, typeCode, oldGender=null, newGender=null, oldAttrac=null, newAttrac=null
         *
         * readDate = null
         * to
         * from
         * typeCode -> 좋아요인지 호감사유 변경인지
         *
         * oldGender -> 만약 성별이 바뀌었다면 저장
         * newGender -> ''
         *
         * oldAttractiveTypeCode -> 호감사유가 변경되었다면 저장
         * newAttractiveTypeCode -> ''
         */
