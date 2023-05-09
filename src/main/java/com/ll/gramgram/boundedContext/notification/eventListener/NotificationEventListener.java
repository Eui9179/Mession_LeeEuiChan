package com.ll.gramgram.boundedContext.notification.eventListener;

import com.ll.gramgram.base.event.EventAfterLike;
import com.ll.gramgram.base.event.EventAfterModifyAttractiveType;
import com.ll.gramgram.boundedContext.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationService notificationService;

    @EventListener
    public void listen(EventAfterLike event) {
        notificationService.makeLike(event.getLikeablePerson());
    }

    @EventListener
    public void listen(EventAfterModifyAttractiveType event) {
        notificationService
                .makeModifyAttractive(event.getLikeablePerson(), event.getOldAttractiveTypeCode(), event.getNewAttractiveTypeCode());
    }
}
