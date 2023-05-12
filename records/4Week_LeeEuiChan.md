# 4Week 이의찬

## 미션 요구사항 분석 & 체크리스트

- [x] 네이버클라우드플랫폼을 통한 배포, 도메인 HTTPS까지 적용
- [x] 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 성별 필터링기능 구현
- [x] 선택미션 - 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 호감사유 필터링기능 구현
- [x] 선택미션 - 내가 받은 호감리스트(/usr/likeablePerson/toList)에서 정렬기능
- [ ] 선택미션 - 젠킨스를 통해서 리포지터리의 main 브랜치에 커밋 이벤트가 발생하면 자동으로 배포가 진행되도록

## 4주차 미션 요약

[접근방법]
1. 네이버클라우드플랫폼을 통한 배포
   - 강의대로 진행

2. Query String
   - SearchFilter을 선언하고 `@ModelAttribute`을 통해 받음
    ```java
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/toList")
    public String showToList(Model model, SearchFilter toListSearchForm) {
        InstaMember instaMember = rq.getMember().getInstaMember();

        // 인스타인증을 했는지 체크
        if (instaMember != null) {
            // 해당 인스타회원이 좋아하는 사람들 목록
            RsData likeablePeople = likeablePersonService.findByToInstaMemberWithFilter(
                    instaMember, toListSearchForm.gender, toListSearchForm.attractiveTypeCode, toListSearchForm.sortCode
            );
            model.addAttribute("likeablePeople", likeablePeople.getData());
        }

        return "usr/likeablePerson/toList";
    }

    @Setter
    public static class SearchFilter {
        private String gender;
        private Integer attractiveTypeCode;
        private int sortCode = 1; // 기본값
    }
    ```

3. Query Dsl 코드

    ```java
    @Override
    public List<LikeablePerson> findQslByToInstaMemberWithFilter(
            InstaMember toInstaMember, String gender, Integer attractiveTypeCode, int sortCode) {
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
    ```

4. 호감리스트 성별 필터링기능 구현
   - QueryDsl을 이용하여 해결
   - `BooleanExpression`을 활용하여 조건 분리
    ```java
    private BooleanExpression eqToInstaMember(InstaMember toInstaMember) {
        return likeablePerson.toInstaMember.eq(toInstaMember);
    }
    
    private BooleanExpression eqGender(String gender) {
        if (gender == null || gender.equals("")) {
            return null;
        }
        return likeablePerson.fromInstaMember.gender.eq(gender);
    }
    ```
5. 호감사유
   - queryDsl을 활용하여 해결
   - 2번과 같은 방식으로 해결
   ```java
    private BooleanExpression eqAttractiveTypeCode(Integer attractiveTypeCode) {
        if (attractiveTypeCode == null) {
            return null;
        }
        return likeablePerson.attractiveTypeCode.eq(attractiveTypeCode);
    }
    ```
6. 정렬 기능
   - queryDsl을 활용하여 해결
   - orderBy에 OrderSpecifier 배열로 넘길 수 있기 때문에 `sortCode`의 리졸버를 만들어 정렬조건을 배열로 반환하였다.
    ```java
    public Q orderBy(OrderSpecifier<?>... o) {
        return queryMixin.orderBy(o);
    }
    ```
   - 코드
    ```java
    private OrderSpecifier[] resolveSortCode(Integer sortCode) {
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        switch (sortCode) {
            case 2 -> orderSpecifiers.add(likeablePerson.modifyDate.asc());
            case 3 -> orderSpecifiers.add(likeablePerson.fromInstaMember.toLikeablePeople.size().desc());
            case 4 -> orderSpecifiers.add(likeablePerson.fromInstaMember.toLikeablePeople.size().asc());
            case 5 -> {
                orderSpecifiers.add(likeablePerson.fromInstaMember.gender.desc());
                orderSpecifiers.add(likeablePerson.modifyDate.desc());
            }
            case 6 -> {
                orderSpecifiers.add(likeablePerson.attractiveTypeCode.asc());
                orderSpecifiers.add(likeablePerson.modifyDate.desc());
            }
            default -> orderSpecifiers.add(likeablePerson.modifyDate.desc());
        }
        return orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]);
    }
    ```

[특이사항]
- 3주차 배포 21강까지 완료하였지만 Springboot앱이 실행이 안되는 오류 해결
   - 문제점: application.yml에 ssl관련된 설정을 빼지 않았기 때문에 발생

- SearchFilter를 통해 쿼리 스트링을 받는데 이 경우 조건이 추가되면 매번 Request DTO와 Service, QueryDsl을 모두 수정해야 한다. 이럴 때는 Map으로 받으로 받아서 자동으로 처리하는 게 맞을까?