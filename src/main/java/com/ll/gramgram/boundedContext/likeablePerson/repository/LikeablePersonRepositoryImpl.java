package com.ll.gramgram.boundedContext.likeablePerson.repository;

import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ll.gramgram.boundedContext.likeablePerson.entity.QLikeablePerson.likeablePerson;

@RequiredArgsConstructor
@Slf4j
public class LikeablePersonRepositoryImpl implements LikeablePersonRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Optional<LikeablePerson> findQslByFromInstaMemberIdAndToInstaMember_username(long fromInstaMemberId, String toInstaMemberUsername) {
        return Optional.ofNullable(
                jpaQueryFactory
                        .selectFrom(likeablePerson)
                        .where(
                                likeablePerson.fromInstaMember.id.eq(fromInstaMemberId)
                                        .and(
                                                likeablePerson.toInstaMember.username.eq(toInstaMemberUsername)
                                        )
                        )
                        .fetchOne()
        );
    }

    @Override
    public List<LikeablePerson> findQslByToInstaMemberWithFilter(
            InstaMember toInstaMember, String gender, Integer attractiveTypeCode, int sortCode) {
        if (toInstaMember == null) {
            return null;
        }

        return jpaQueryFactory
                .selectFrom(likeablePerson)
                .where(
                        eqToInstaMember(toInstaMember),
                        eqGender(gender),
                        eqAttractiveTypeCode(attractiveTypeCode)
                )
                .orderBy(resolveSortCode(sortCode))
                .fetch();
    }

    private BooleanExpression eqToInstaMember(InstaMember toInstaMember) {
        return likeablePerson.toInstaMember.eq(toInstaMember);
    }

    private BooleanExpression eqGender(String gender) {
        if (gender == null || gender.equals("")) {
            return null;
        }
        return likeablePerson.fromInstaMember.gender.eq(gender);
    }

    private BooleanExpression eqAttractiveTypeCode(Integer attractiveTypeCode) {
        if (attractiveTypeCode == null) {
            return null;
        }
        return likeablePerson.attractiveTypeCode.eq(attractiveTypeCode);
    }

    private OrderSpecifier[] resolveSortCode(Integer sortCode) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        switch (sortCode) {
            case 2 -> orderSpecifiers.add(likeablePerson.id.asc());
            case 3 -> orderSpecifiers.add(likeablePerson.fromInstaMember.toLikeablePeople.size().desc());
            case 4 -> orderSpecifiers.add(likeablePerson.fromInstaMember.toLikeablePeople.size().asc());
            case 5 -> {
                orderSpecifiers.add(likeablePerson.fromInstaMember.gender.desc());
                orderSpecifiers.add(likeablePerson.id.desc());
            }
            case 6 -> {
                orderSpecifiers.add(likeablePerson.attractiveTypeCode.asc());
                orderSpecifiers.add(likeablePerson.id.desc());
            }
            default -> orderSpecifiers.add(likeablePerson.id.desc());
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
}
