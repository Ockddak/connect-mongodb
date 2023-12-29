 # 부동산 매매 정보 API 서비스
 공공데이터 포털에 존재하는 날짜별 법정동 코드별 부동산 매매 정보들을 가져와 전처리하여 서버에 저장하고 해당 데이터들을 응답해주는 API

 # 개발 환경
 - Windows 10
 - Java 11
 - MongoDB 7
 - PostgreSQL 15
 - SpringBoot 2.7.5

 # 빌드 및 실행 방법
 ``` bash
$ ./gradlew build
$ cd build/libs
$ java -jar connect-mongodb-0.0.1-SNAPSHOT.jar
```

# Stack
## Environment
![Git](https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=Git&logoColor=white)
![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=GitHub&logoColor=white)
![Intellij](https://img.shields.io/badge/IntelliJ-000000?style=for-the-badge&logo=IntelliJIDEA&logoColor=white)

## Config
![Gradle](https://img.shields.io/badge/Gradle-02303A?style=for-the-badge&logo=Gradle&logoColor=white)

## Operation
![Docker](https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=Docker&logoColor=white)

## Development
![Java](https://img.shields.io/badge/JAVA-007396?style=for-the-badge&logo=java&logoColor=white)
![Spring Boot](https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=SpringBoot&logoColor=white)
![JPA](https://img.shields.io/badge/JPA-6DB33F?style=for-the-badge&logo=JPA&logoColor=white)

## DataBase
![PostgreSQL](https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=MariaDB&logoColor=white)
![MongoDB](https://img.shields.io/badge/Redis-DC382D?style=for-the-badge&logo=Redis&logoColor=white)

# 추후 추가 기능
- 현재는 연립 주택만 보여주고 있으나 추후에는 연립 주택 외에 다른 부동산 매매 타입(아파트, 오피스텔 등) 데이터들도 처리 가능하도록 추가
- 페이징 기능 추가
- 사용자를 검증하도록 하는 기능을 추가
