package com.ll.gramgram.boundedContext.notification.service;

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

    @Transactional
    public void whenAfterLike(LikeablePerson likeablePerson) {
        Notification notification = Notification.builder()
                .toInstaMember(likeablePerson.getToInstaMember())
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .typeCode(TypeCode.LIKE)
                .build();
        notificationRepository.save(notification);
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Transactional
    public void whenAfterModifyAttractiveType(LikeablePerson likeablePerson, int oldAttractiveTypeCode, int newAttractiveTypeCode) {
        Notification notification = Notification.builder()
                .toInstaMember(likeablePerson.getToInstaMember())
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .newAttractiveTypeCode(newAttractiveTypeCode)
                .typeCode(TypeCode.MODIFY_ATTRACTIVE_TYPE)
                .build();
        notificationRepository.save(notification);
    }

}