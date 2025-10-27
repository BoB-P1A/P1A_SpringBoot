---
name: Bug report
about: 버그 제보
title: BUG
labels: ''
assignees: 10000dooong

---

name: "🐞 Bug"
description: "버그 제보"
title: "Bug: bug_title"
labels: ["bug"]
assignees: ["Fragarian"]

body:
  - type: markdown
    attributes:
      value: "버그 제보에 감사드립니다! 아래 항목들을 최대한 상세하게 작성해주세요."
  - type: textarea
    id: current-behavior
    attributes:
      label: 🔍 현재 동작 (Current Behavior)
      description: "현재 발생하고 있는 문제를 설명해 주세요."
      placeholder: "예: 로그인 버튼을 눌렀을 때 아무 반응이 없습니다."
    validations:
      required: true
  - type: textarea
    id: expected-behavior
    attributes:
      label: ✅ 기대했던 동작 (Expected Behavior)
      description: "원래 어떤 동작을 기대하셨나요?"
      placeholder: "예: 로그인 버튼을 누르면 로그인 페이지로 이동합니다."
    validations:
      required: true
  - type: textarea
    id: reproduction-steps
    attributes:
      label: 🐞 재현 방법 (Steps to Reproduce)
      description: "버그를 재현하는 단계들을 명확히 알려주세요."
      placeholder: >
        예시:
        1. 페이지 접속
        2. 버튼 클릭
        3. 입력
    validations:
      required: true
  - type: dropdown
    id: os
    attributes:
      label: 🖥️ 운영체제 (OS)
      description: "버그가 발생한 운영체제를 선택해 주세요. (기타 정보는 아래에 추가)"
      options:
        - Windows
        - macOS
        - Linux
        - Android
        - iOS
        - 기타
      default: 0
    validations:
      required: true
  - type: input
    id: os-details
    attributes:
      label: "운영체제 추가 정보"
      description: "운영체제 버전 또는 기타 정보를 입력해 주세요."
      placeholder: "예: Ubuntu 22.04"
  - type: dropdown
    id: browser
    attributes:
      label: 🌐 브라우저 (Browser)
      description: "버그가 발생한 브라우저를 선택해 주세요."
      options:
        - Chrome
        - Firefox
        - Edge
        - Safari
        - 기타
  - type: dropdown
    id: ide
    attributes:
      label: 💻 IDE
      description: "버그와 관련된 IDE를 선택해 주세요."
      options:
        - PyCharm
        - Visual Studio Code
        - IntelliJ IDEA
        - Visual Studio
        - WebStorm
        - Android Studio
        - Xcode
        - 기타
      default: 0
  - type: input
    id: ide-other
    attributes:
      label: "IDE 추가 정보 (목록에 없을 경우)"
      description: "위 목록에 없는 IDE는 여기에 직접 입력해 주세요."
      placeholder: "예: Vim, Eclipse"
    validations:
      required: false
