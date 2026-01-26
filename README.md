# SimpleGmailSender

![Kotlin](https://img.shields.io/badge/Kotlin-100%25-blueviolet) ![License](https://img.shields.io/badge/License-MIT-blue)

`SimpleGmailSender`는 Gmail의 SMTP 서버를 통해 이메일을 쉽게 보낼 수 있도록 도와주는 가벼운 코틀린 라이브러리입니다. 복잡한 설정 없이, 몇 줄의 코드만으로 동기 및 비동기 이메일 발송 기능을 구현할 수 있습니다.

## ✨ 주요 기능

- **간편한 사용법**: 직관적인 API를 통해 이메일을 쉽게 보낼 수 있습니다.
- **동기 및 비동기 지원**: 코루틴을 활용한 비동기 메서드를 지원하여 Non-blocking I/O 처리가 가능합니다.
- **대량 발송**: 단일 SMTP 연결을 통해 여러 이메일을 효율적으로 전송하고, 성공/실패 결과를 반환합니다.
- **보안**: Gmail 계정 비밀번호 대신 [앱 비밀번호](https://support.google.com/accounts/answer/185833) 사용을 권장합니다.
- **유연한 설정**: 기본 Gmail SMTP 설정 외에 커스텀 SMTP 서버 정보(호스트, 포트)를 직접 지정할 수 있습니다.

## 📦 설치 방법

**Gradle (Kotlin DSL)**

`build.gradle.kts` 파일의 `dependencies` 블록에 다음 코드를 추가하세요.

```kotlin
repositories {
    mavenCentral()
    // GitHub Packages 저장소 추가
    maven { url = uri("https://maven.pkg.github.com/picbel/SimpleGmailSender") }
}

dependencies {
    implementation("com.picbel:simple-gmail-sender:1.0.0") // 버전을 최신 릴리즈 버전으로 변경하세요.
}
```

## 🚀 사용법

### 1. SimpleGmailSender 인스턴스 생성

`SimpleGmailSender.of()` 팩토리 메서드를 사용하여 인스턴스를 생성합니다.

> 🔒 **중요**: Gmail 계정의 보안을 위해, 일반 비밀번호 대신 **앱 비밀번호**를 생성하여 사용하세요.
> [Google 계정 2단계 인증](https://support.google.com/accounts/answer/185833)을 활성화한 후 앱 비밀번호를 발급받을 수 있습니다.

```kotlin
import com.picbel.sender.SimpleGmailSender

fun main() {
    val username = "your-gmail-address@gmail.com"
    val appPassword = "your-generated-app-password"

    val sender = SimpleGmailSender.of(username, appPassword)

    // 이제 'sender'를 사용하여 이메일을 보낼 수 있습니다.
}
```

### 2. 이메일 한 건 보내기 (동기)

`send()` 메서드를 사용하여 이메일을 즉시 보냅니다.

```kotlin
import com.picbel.sender.SimpleGmailSender.EmailMessage

val message = EmailMessage(
    to = "recipient@example.com",
    subject = "안녕하세요! SimpleGmailSender 테스트입니다.",
    body = "이것은 동기 방식으로 발송된 테스트 이메일입니다."
)

try {
    sender.send(message)
    println("이메일 전송 성공!")
} catch (e: Exception) {
    println("이메일 전송 실패: ${e.message}")
}
```

### 3. 이메일 한 건 보내기 (비동기)

코루틴 내에서 `sendAsync()` 메서드를 사용하여 이메일을 비동기적으로 보냅니다.

```kotlin
import kotlinx.coroutines.runBlocking

runBlocking {
    val message = EmailMessage(
        to = "recipient@example.com",
        subject = "비동기 이메일 테스트",
        body = "이것은 코루틴을 통해 비동기 방식으로 발송된 테스트 이메일입니다."
    )

    try {
        sender.sendAsync(message)
        println("비동기 이메일 전송 요청 성공!")
    } catch (e: Exception) {
        println("비동기 이메일 전송 실패: ${e.message}")
    }
}
```

### 4. 여러 이메일 보내기 (대량 발송)

`sendBulk()` 메서드는 단일 연결을 사용하여 여러 이메일을 효율적으로 보냅니다. 각 이메일의 전송 성공 및 실패 여부를 `BulkSendResult`로 반환합니다.

```kotlin
val messages = listOf(
    EmailMessage("user1@example.com", "대량 발송 1", "첫 번째 이메일입니다."),
    EmailMessage("user2@example.com", "대량 발송 2", "두 번째 이메일입니다."),
    EmailMessage("invalid-email", "대량 발송 3", "이 이메일은 실패할 것입니다.")
)

val result = sender.sendBulk(messages)

println("✅ 성공한 이메일: ${result.successful.size}건")
result.successful.forEach { println("  - ${it.to}") }

println("❌ 실패한 이메일: ${result.failed.size}건")
result.failed.forEach { (msg, ex) ->
    println("  - ${msg.to}: ${ex.message}")
}
```
`sendBulkAsync()`를 사용하면 대량 발송을 비동기적으로 처리할 수도 있습니다.

## ⚙️ 커스텀 SMTP 설정

Gmail이 아닌 다른 SMTP 서버를 사용하거나, 다른 포트를 사용해야 할 경우 호스트와 포트 정보를 직접 지정할 수 있습니다.

```kotlin
val customSender = SimpleGmailSender.of(
    host = "smtp.your-provider.com",
    port = "465", // 예: SMTPS 포트
    username = "user@your-provider.com",
    password = "password"
)
```

## 📜 라이선스

이 프로젝트는 `MIT` 라이선스를 따릅니다. 자세한 내용은 `LICENSE` 파일을 참고하세요.
(아직 LICENSE 파일이 없다면 추가하는 것을 권장합니다.)

---
