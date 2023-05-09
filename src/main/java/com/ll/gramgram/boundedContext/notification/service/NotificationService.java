package com.ll.gramgram.boundedContext.notification.service;

import com.ll.gramgram.base.rsData.RsData;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.notification.dto.NotReadNotification;
import com.ll.gramgram.boundedContext.notification.entity.Notification;
import com.ll.gramgram.boundedContext.notification.entity.TypeCode;
import com.ll.gramgram.boundedContext.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final NotificationRepository notificationRepository;

    public List<Notification> findByToInstaMember(InstaMember toInstaMember) {
        return notificationRepository.findByToInstaMemberOrderByIdDesc(toInstaMember);
    }

    public boolean isNotRead(InstaMember toInstaMember) {
        return notificationRepository.countByToInstaMemberAndReadDateIsNull(toInstaMember) > 0;
    }

    @Transactional
    public RsData findByToInstaMemberNotRead(InstaMember toInstaMember) {
        List<Notification> notifications = notificationRepository
                .findByToInstaMemberAndReadDateIsNullOrderByCreateDateDesc(toInstaMember);
        List<NotReadNotification> notReadNotifications = new ArrayList<>();

        for (Notification notification : notifications) {
//            notification.read(LocalDateTime.now()); // 읽음으로 표시
            InstaMember fromInstaMember = notification.getFromInstaMember();

            if (notification.getTypeCode().equals(TypeCode.LIKE)) {
                NotReadNotification notReadNotification = NotReadNotification.builder()
                        .dateTime(genTimeString(notification.getCreateDate()))
                        .fromInstaMemberUsername(fromInstaMember.getUsername())
                        .gender(fromInstaMember.getGenderDisplayName())
                        .typeCode(TypeCode.LIKE)
                        .newAttractiveType(notification.getNewAttractiveTypeDisplayName())
                        .build();

                notReadNotifications.add(notReadNotification);
            } else if (notification.getTypeCode().equals(TypeCode.MODIFY_ATTRACTIVE_TYPE)) {
                NotReadNotification notReadNotification = NotReadNotification.builder()
                        .dateTime(genTimeString(notification.getCreateDate()))
                        .fromInstaMemberUsername(notification.getFromInstaMember().getUsername())
                        .gender(fromInstaMember.getGenderDisplayName())
                        .typeCode(TypeCode.MODIFY_ATTRACTIVE_TYPE)
                        .oldAttractiveType(notification.getOldAttractiveTypeDisplayName())
                        .newAttractiveType(notification.getNewAttractiveTypeDisplayName())
                        .build();

                notReadNotifications.add(notReadNotification);
            }
        }
        return RsData.of("S-1", "", notReadNotifications);
    }

    @Transactional
    public RsData markAsRead(List<Notification> notifications) {
        notifications
                .stream()
                .filter(notification -> !notification.isRead())
                .forEach(Notification::markAsRead);

        return RsData.of("S-1", "읽음 처리 되었습니다.");
    }

    @Transactional
    public void makeLike(LikeablePerson likeablePerson) {
        Notification notification = Notification.builder()
                .toInstaMember(likeablePerson.getToInstaMember())
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .newAttractiveTypeCode(likeablePerson.getAttractiveTypeCode())
                .newGender(likeablePerson.getFromInstaMember().getGender())
                .typeCode(TypeCode.LIKE)
                .build();
        notificationRepository.save(notification);
    }

    public List<Notification> findAll() {
        return notificationRepository.findAll();
    }

    @Transactional
    public void makeModifyAttractive(LikeablePerson likeablePerson, int oldAttractiveTypeCode, int newAttractiveTypeCode) {
        Notification notification = Notification.builder()
                .toInstaMember(likeablePerson.getToInstaMember())
                .fromInstaMember(likeablePerson.getFromInstaMember())
                .oldAttractiveTypeCode(oldAttractiveTypeCode)
                .newAttractiveTypeCode(newAttractiveTypeCode)
                .newGender(likeablePerson.getFromInstaMember().getGender())
                .typeCode(TypeCode.MODIFY_ATTRACTIVE_TYPE)
                .build();
        notificationRepository.save(notification);
    }

    private String genTimeString(LocalDateTime createDateTime) {
        StringBuilder sb = new StringBuilder();

        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(createDateTime, now);

        sb.append(createDateTime.getYear())
                .append(createDateTime.getMonth())
                .append(createDateTime.getDayOfMonth())
                .append(", ")
                .append(genTimeAgoString(duration));

        return sb.toString();
    }

    private String genTimeAgoString(Duration duration) {
        long day = duration.toDays();
        long hour = duration.toHours() % 24;
        long minute = duration.toMinutes() % 60;
        long second = duration.getSeconds();

        if (day > 0) {
            return day + "일 전";
        } else if (hour > 0) {
            return hour + "시간 전";
        } else if (minute > 0) {
            return minute + "분 전";
        }
        return second + "초 전";
    }
}