# 2Week 이의찬


## 미션 요구사항 분석 & 체크리스트

- [x] 중복 호감 표시
- [x] 호감 상대 개수 제한
- [x] 호감 사유 업데이트

## 2주차 미션 요약

[접근방법]

### 중복 처리
Controller에서 checkDuplicateOrUpdate() 함수로 처리

1. 데이터 없음 -> S-1
2. attractiveTypeCode가 다르면 업데이트 처리 -> S-2
3. attractiveTypeCode가 같으면 중복 처리 -> F-3

### 10명 제한

Controller에서 `checkCountLessThanMax()`로 처리

