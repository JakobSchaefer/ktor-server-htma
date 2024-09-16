# HTMA - HTML-Driven Fullstack Web Application Framework

HTMA is a new fullstack web application framework.
- Easy to learn
- Driven by HTML
- Powered by [Kotlin](https://kotlinlang.org/) & [Ktor](https://ktor.io/)

**Usage:**
```kotlin
fun main() {
  embeddedServer(Netty, port = 8080) {
    install(HypertextMarkupApplication)
    
    routing {
      web {
        page("/hello") {
          data {
            put("name", "HTMA")
          }
        }
      }
    }
  }.start(wait = true)
}
```

```html
<!-- File: web/hello.html -->
<!doctype html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>HTMA Demo</title>
</head>
<body>
    <!-- use tailwind for styling -->
    <h1 class="text-2xl">
        Hello
        <!-- use thymeleaf for templating -->
        <span class="font-bold" data-th-text="${name}">
            World
        </span>
    </h1>
</body>
</html>
```
