# Non-Member TimeCapsule Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Implement a non-member time capsule service where users bury messages with virtual coordinates and receive them via email.

**Architecture:** Refactor `LetterPaper` to store sender/receiver info and virtual coordinates. Implement a virtual biome system based on (X, Y) coordinates. Use a background scheduler for email delivery and email/password for retrieval.

**Tech Stack:** Spring Boot (Java 17), JPA/H2, Vue.js, Jakarta Mail.

---

### Task 1: Update LetterPaper Entity

**Files:**
- Modify: `src/main/java/com/mini/timecapsule/model/LetterPaper.java`

- [ ] **Step 1: Update Entity Fields**
Add fields for sender, receiver email, password, coordinates, and biome. Remove `User` dependency.

```java
// Add these fields and remove User user
private String senderName;
private String receiverEmail;
private String password; // Plain for now, or BCrypt
private Integer xCoord;
private Integer yCoord;
private String biome;
private String locationName;
```

- [ ] **Step 2: Update static factory method `newEntity`**
Update the parameters to match the new fields.

- [ ] **Step 3: Commit**
```bash
git add src/main/java/com/mini/timecapsule/model/LetterPaper.java
git commit -m "model: update LetterPaper entity for non-member flow and coordinates"
```

### Task 2: Implement Coordinate and Biome Logic

**Files:**
- Create: `src/main/java/com/mini/timecapsule/utils/LocationMapper.java`

- [x] **Step 1: Create LocationMapper Utility**
Implement logic to map (X, Y) to biomes.

```java
public class LocationMapper {
    public static String getBiome(int x, int y) {
        if (x < 500 && y < 500) return "BEACH";
        if (x >= 500 && y < 500) return "MOUNTAIN";
        if (x < 500 && y >= 500) return "DESERT";
        return "FOREST";
    }
    
    public static String getBiomeName(String biome) {
        switch(biome) {
            case "BEACH": return "고요한 바다";
            case "MOUNTAIN": return "바람의 능선";
            case "DESERT": return "붉은 사막";
            case "FOREST": return "신비한 숲";
            default: return "알 수 없는 곳";
        }
    }
}
```

- [x] **Step 2: Commit**
```bash
git add src/main/java/com/mini/timecapsule/utils/LocationMapper.java
git commit -m "util: add LocationMapper for virtual coordinates"
```

### Task 3: Refactor LetterService - Creation

**Files:**
- Modify: `src/main/java/com/mini/timecapsule/service/LetterService.java`
- Create: `src/main/java/com/mini/timecapsule/dto/CapsuleCreateRequest.java`

- [ ] **Step 1: Create DTO**
```java
public class CapsuleCreateRequest {
    private String senderName;
    private String receiverEmail;
    private String password;
    private String content;
    private ZonedDateTime openAt;
    // ... getters/setters
}
```

- [ ] **Step 2: Implement `sendLetter` with random coordinates**
Generate random X, Y and use `LocationMapper`.

- [ ] **Step 3: Commit**
```bash
git add src/main/java/com/mini/timecapsule/service/LetterService.java src/main/java/com/mini/timecapsule/dto/CapsuleCreateRequest.java
git commit -m "service: refactor sendLetter to support non-member flow and random coordinates"
```

### Task 4: Refactor LetterService - Retrieval

**Files:**
- Modify: `src/main/java/com/mini/timecapsule/service/LetterService.java`
- Modify: `src/main/java/com/mini/timecapsule/dao/LetterPaperRepository.java`

- [ ] **Step 1: Update Repository**
Add `findByReceiverEmailAndPassword`.

- [ ] **Step 2: Implement `listByEmail` and `getDetail`**
`getDetail` should hide `content` if `now < openAt`.

- [ ] **Step 3: Commit**
```bash
git add src/main/java/com/mini/timecapsule/service/LetterService.java src/main/java/com/mini/timecapsule/dao/LetterPaperRepository.java
git commit -m "service: implement capsule retrieval by email and password"
```

### Task 5: Implement Email Scheduler

**Files:**
- Modify: `src/main/java/com/mini/timecapsule/service/TimecapsuleScheduler.java`

- [ ] **Step 1: Implement Email Sending Logic**
Use `JavaMailSender` to send emails. (Need to add `spring-boot-starter-mail` to `build.gradle` if not present).

- [ ] **Step 2: Commit**
```bash
git add src/main/java/com/mini/timecapsule/service/TimecapsuleScheduler.java
git commit -m "service: implement automated email delivery for opened capsules"
```

### Task 6: Update LetterController

**Files:**
- Modify: `src/main/java/com/mini/timecapsule/controller/LetterController.java`

- [ ] **Step 1: Change API base to `/api/v1/capsule`**
- [ ] **Step 2: Update endpoints for creation and list**
- [ ] **Step 3: Commit**
```bash
git add src/main/java/com/mini/timecapsule/controller/LetterController.java
git commit -m "controller: update API endpoints for new capsule flow"
```

### Task 7: Frontend - Update Write Screen

**Files:**
- Modify: `vue/src/components/WriteLetter.vue`
- Modify: `vue/src/components/SelectCapsule.vue` (to handle input)

- [ ] **Step 1: Add email and password inputs to the flow**
- [ ] **Step 2: Update `submitLetter` to call new API**
- [ ] **Step 3: Commit**
```bash
git add vue/src/components/WriteLetter.vue
git commit -m "feat: add email and password fields to writing flow"
```

### Task 4: Frontend - Update Retrieval (Find My Capsule)

**Files:**
- Modify: `vue/src/components/myCapsule.vue`
- Create: `vue/src/components/FindCapsule.vue`

- [ ] **Step 1: Create FindCapsule component for email/password entry**
- [ ] **Step 2: Update myCapsule to fetch from API instead of test data**
- [ ] **Step 3: Commit**
```bash
git add vue/src/components/myCapsule.vue vue/src/components/FindCapsule.vue
git commit -m "feat: implement real-time capsule retrieval in frontend"
```

### Task 9: Final Cleanup

**Files:**
- Modify: `vue/src/views/HomeView.vue`
- Modify: `vue/src/router/index.js`

- [ ] **Step 1: Remove Login/Join routes and buttons**
- [ ] **Step 2: Update Home buttons to "Bury" and "Find"**
- [ ] **Step 3: Commit**
```bash
git add vue/src/views/HomeView.vue vue/src/router/index.js
git commit -m "chore: clean up home screen and routes"
```
