## 사전 설치
- JDK 21, Docker Desktop, Git

## 최초 세팅
- git clone → .env.example 복사해서 .env 만들고 채우기

## 평소 개발
- docker compose up -d db (DB만 띄우기)
- IntelliJ Run Configuration에 --spring.profiles.active=local 추가

## 배포 환경 최종 점검 (가끔만)
- ./gradlew build -x test
- docker compose up -d --build

## 참고
- MySQL 포트가 3307인 이유 (로컬 MySQL과 충돌 방지)
- application.yml이 local/docker 프로파일로 분리되어 있음