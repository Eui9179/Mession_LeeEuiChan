
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

1. 필터링
    - SearchFilter을 선언하고 @ModelAttribute을 통해 받음
    ```java
    @Setter
    private static class SearchFilter {
        private String gender;
        private Integer attractiveTypeCode;
        private Integer sortCode = 1; // 기본값
    }
    ```
2. 호감리스트 성별 필터링기능 구현 
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
3. 호감사유 
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
4. 정렬 기능
    - queryDsl을 활용하여 해결
    - orderBy에 OrderSpecifier 배열로 넘길 수 있기 때문에 `sortCode`의 리졸버를 만들어 정렬조건을 배열로 반환하였다.
    ```java
    public Q orderBy(OrderSpecifier<?>... o) {
        return queryMixin.orderBy(o);
    }
    ```

[특이사항]
- 3주차 배포 21강까지 완료하였지만 Springboot앱이 실행이 안되는 오류 해결
    - 문제점: application.yml에 ssl관련된 설정을 빼지 않았기 때문에 발생

- SearchFilter를 통해 쿼리 스트링을 받는데 이 경우 조건이 추가되면 매번 Request DTO와 Service, QueryDsl을 모두 수정해야 한다. 이럴 때는 Map으로 받으로 받아서 자동으로 처리하는 게 맞을까?