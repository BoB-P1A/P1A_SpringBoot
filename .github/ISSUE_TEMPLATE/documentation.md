---
name: Documentation
about: 문서화 작업 이슈
title: Documentation
labels: ''
assignees: 10000dooong

---

name: "📝 Documentation"
description: "문서화 작업 이슈"
title: "Docs: documentation_title"
labels: ["documentation"]
assignees: ["Fragarian"]

body:
  - type: dropdown
    id: os
    attributes:
      label: 📝 문서화 분류
      description: "문서화할 대상을 선택해주세요."
      multiple: true
      options:
        - API References
        - README
        - comment
      default: 0
    validations:
      required: true
  - type: textarea
    attributes:
      label: 📖 문서 내용
      description: "어떤 문서를 작성하거나 수정할 것인지 설명해 주세요."
      placeholder: "새로운 API 문서 추가, README.md 파일 업데이트 등"
    validations:
      required: true
  - type: textarea
    attributes:
      label: ✅ 작업할 내용
      description: "할 일을 체크박스 형태로 작성해주세요."
      value: >
        - [ ]
    validations:
      required: true
  - type: textarea
    attributes:
      label: 📍 참고 자료
      description: "참고 자료가 있다면 작성해 주세요."
      placeholder: " - [기존 문서](https://...)"
