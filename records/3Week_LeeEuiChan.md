
# 3Week 이의찬

## 미션 요구사항 분석 & 체크리스트

- [ ] 네이버클라우드배포
- [x] 호감표시/호감사유변경 후, 개별 호감표시건에 대해서, 3시간 동안은 호감취소와 호감사유변경을 할 수 없도록 작업
- [x] 선택미션 - 알림기능 구현

## 3주차 미션 요약

[접근방법]

1. 3시간 쿨타임
   - 호감표시, 호감사유변경 후, 개별 호감 표시건에 대해서 `isModifyUnlocked()`를 사용하여 수정 및 삭제 방지
   - 시간이 지났다면 변경 가능
2. 알림기능
   - readDate가 null인 데이터만 가져와서 처리 후 현재 시각으로 업데이트
   - NotReadNotification DTO를 통해 model에 데이터 설정
   - rq에서 isNotification()을 통해 readDate가 null인 데이터가 있다면 layout 알림아이콘 표시

[특이사항]
- 배포 21강까지 완료하였지만 Springboot앱이 실행이 안됨
  - 에러
  ```text
    org.springframework.context.ApplicationContextException: Failed to start bean 'webServerStartStop'
    at org.springframework.context.support.DefaultLifecycleProcessor.doStart(DefaultLifecycleProcessor.java:181) ~[spring-context-6.0.7.jar!/:6.0.7]
    ...
    Caused by: java.io.FileNotFoundException: /keystore.p12 (No such file or directory)
    at java.base/java.io.FileInputStream.open0(Native Method) ~[na:na]
    at java.base/java.io.FileInputStream.open(FileInputStream.java:211) ~[na:na]
    at java.base/java.io.FileInputStream.<init>(FileInputStream.java:153) ~[na:na]
    at java.base/java.io.FileInputStream.<init>(FileInputStream.java:108) ~[na:na]
    at java.base/sun.net.www.protocol.file.FileURLConnection.connect(FileURLConnection.java:86) ~[na:na]
    at java.base/sun.net.www.protocol.file.FileURLConnection.getInputStream(FileURLConnection.java:189) ~[na:na]
    at org.apache.catalina.startup.CatalinaBaseConfigurationSource.getResource(CatalinaBaseConfigurationSource.java:118) ~[tomcat-embed-core-10.1.7.jar!/:na]
    at org.apache.tomcat.util.net.SSLUtilBase.getStore(SSLUtilBase.java:200) ~[tomcat-embed-core-10.1.7.jar!/:na]
    at org.apache.tomcat.util.net.SSLHostConfigCertificate.getCertificateKeystore(SSLHostConfigCertificate.java:207) ~[tomcat-embed-core-10.1.7.jar!/:na]
    at org.apache.tomcat.util.net.SSLUtilBase.getKeyManagers(SSLUtilBase.java:282) ~[tomcat-embed-core-10.1.7.jar!/:na]
    at org.apache.tomcat.util.net.SSLUtilBase.createSSLContext(SSLUtilBase.java:246) ~[tomcat-embed-core-10.1.7.jar!/:na]
    at org.apache.tomcat.util.net.AbstractJsseEndpoint.createSSLContext(AbstractJsseEndpoint.java:104) ~[tomcat-embed-core-10.1.7.jar!/:na]
    ... 33 common frames omitted
    ```