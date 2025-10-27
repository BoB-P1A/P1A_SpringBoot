---
name: Feature
about: 새로운 기능 제안
title: ''
labels: ''
assignees: 10000dooong

---

name: "✨ Feature"
description: "새로운 기능 제안"
title: "Feature: feature_title"
labels: ["enhancement"]
assignees: ["Fragarian"]

body:
  - type: textarea
    attributes:
      label: 📄 이슈 설명
      description: "새로운 기능에 대한 설명을 작성해 주세요."
      placeholder: "자세히 적을수록 좋습니다."
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
      description: 참고 자료가 있다면 작성해 주세요.
      placeholder: " - [Title](https://...)"
