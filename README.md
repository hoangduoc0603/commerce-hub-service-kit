# commerce-hub service-kit (đã ngừng dùng)

> **Không dùng từ 2026-11-15.** Repo được archive và giữ lại làm lịch sử. Dự án commerce-hub thôi dùng thư viện dùng chung: mỗi service là một project Spring Initializr tự chứa, giữ mặc định của Initializr, layout hexagonal là quy ước. Lý do và phương án đã loại nằm ở ADR-0018 trong repo [`commerce-hub-docs`](https://github.com/hoangduoc0603/commerce-hub-docs) (`adr/0018-self-contained-services-no-shared-library-yet.md`).

Thư viện được viết ở ticket G0-18 (`.scratch/g0-platform/issues/18-service-kit-v0.md` trong repo docs), gồm hai module:

- `gradle-plugin`: convention plugin `commercehub.spring-service`. Plugin ghim Java 25 và Spring Boot 4.1, dùng BOM của Boot qua `platform()`, thêm `test-support` vào classpath test.
- `test-support`: luật ArchUnit cho layout hexagonal (`HexagonalRules`), cấu hình Testcontainers PostgreSQL, bảng tag image.

Bản đã phát hành: `0.1.0` và `0.1.1` trên GitHub Packages (giữ nguyên, không xóa). Release được khóa ở hai lớp: tag `v*` khóa bằng ruleset, và GitHub Packages trả `409` khi publish lại một version đã có.

Những phần có thể dùng lại nếu dự án xét lại việc tách thư viện ở Giai đoạn 1:

- Cách viết luật ArchUnit cho phép package rỗng, kèm fixture vi phạm để test chính các luật đó.
- Convention plugin không khai repository, và test nó bằng TestKit.
- Composite build `--include-build`.
- Publish chỉ từ tag nằm trên nhánh mặc định.

Chuẩn chi tiết: `standards/java-library.md` trong repo docs.
