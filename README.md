# DomeGuard v1.1.0

Плагин для Purpur/Paper 1.21.4.

## Возможности

- `/dome` — GUI управления куполом.
- Центр X/Z можно поставить на текущую позицию.
- Радиус купола.
- Верхняя и нижняя границы Y.
- Тошнота за пределами.
- Darkness на глубине, заданной в config.yml.
- Урон увеличивается с каждым блоком за границей.
- Смерть в критической зоне.
- Автоматический респавн через 3 секунды по умолчанию.
- Настройки сохраняются в config.yml.

## Сборка

Требуется Java 21 и Maven:

```bash
mvn clean package
```

JAR:

```text
target/DomeGuard-1.1.0.jar
```

## GitHub Actions

Workflow `.github/workflows/build.yml` автоматически собирает JAR при push в main/master.

Для ручной сборки:
GitHub → Actions → Build DomeGuard → Run workflow.

После сборки:
Actions → последний запуск → Artifacts → DomeGuard-1.1.0.
