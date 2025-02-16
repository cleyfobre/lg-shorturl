# 📌 Short URL Service

---

## 🛠 환경 구성

- **Java**: 17
- **Spring Boot**: 3.4.2
- **Gradle**: 8.12.1

---

## 📌 API 목록

### 1️⃣ 단축 URL 생성 API

- 원본 URL을 입력하면 단축 URL을 반환합니다.

### 2️⃣ 원본 URL 조회 API

- 단축 URL을 입력하면 원본 URL을 반환합니다.

---

## 🔍 로직 설명

1. **SHA-256 알고리즘**을 사용하여 원본 URL을 해시 값으로 변환 (초기 salt 값은 `0`)
2. **URL에 안전한 문자열**(영어 소문자, 숫자, 하이픈 `-`)로 인코딩 (1차 도메인 제외, 5글자로 제한)
3. 변환된 shortUrl이 **이미 존재하면**, `salt + 1` 값을 추가하여 다시 1단계부터 실행
4. **처음 생성된 shortUrl이라면** 저장 후 반환

---

## ✅ 테스트 코드

📌 **단위 테스트 파일**

- `src/test/java/com/laundrygo/shorturl/ShorturlApplicationTests.java`

📌 **Rest Client를 사용하는 경우**

- `src/test/java/com/laundrygo/shorturl/rest/shortUrl.rest`

---

## 📌 실행 방법

```sh
# Gradle 빌드 및 실행
./gradlew clean build
./gradlew bootRun
```
