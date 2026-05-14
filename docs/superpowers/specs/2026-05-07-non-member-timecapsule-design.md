# Spec: Non-Member TimeCapsule Service with Virtual Coordinates

## 1. Overview
A simple, non-member oriented time capsule service where users can "bury" messages to be sent via email on a specific date. The service uses a unique virtual coordinate system to give each capsule a "physical" location in a virtual world.

## 2. Core Features
- **Capsule Creation:** No login required. Users provide sender name, receiver email, content, open date, and a private password.
- **Virtual Coordinates:** Every capsule is assigned a random (X, Y) coordinate (0-999) and a corresponding "Biome" name (Beach, Mountain, Desert, Forest).
- **Email Delivery:** A background scheduler checks for capsules that have reached their `openAt` date and sends an email to the receiver.
- **Capsule Retrieval:** Users can search for their buried capsules using their email and the specific capsule's password.

## 3. Technical Design

### 3.1 Data Model (LetterPaper Entity)
Modified to support non-member flow and coordinates:
- `senderName`: String
- `receiverEmail`: String (Identifier for retrieval)
- `password`: String (Encrypted, for retrieval)
- `content`: Text
- `openAt`: ZonedDateTime
- `isOpened`: Boolean
- `xCoord`: Integer (0-999)
- `yCoord`: Integer (0-999)
- `biome`: String (Enum: BEACH, MOUNTAIN, DESERT, FOREST)
- `locationName`: String (e.g., "Silent Beach #125")

### 3.2 Virtual Coordinate Logic
- **Range:** X [0-999], Y [0-999]
- **Biomes:**
  - X [0-499], Y [0-499]: **BEACH** (고요한 바다)
  - X [500-999], Y [0-499]: **MOUNTAIN** (바람의 능선)
  - X [0-499], Y [500-999]: **DESERT** (붉은 사막)
  - X [500-999], Y [500-999]: **FOREST** (신비한 숲)
- **Logic:** Generate random X, Y on creation. Map to Biome. Save as `locationName`.

### 3.3 API Specifications (Base: `/api/v1/capsule`)
- `POST /`: Create a new capsule. Returns assigned coordinates and location name.
- `POST /list`: List capsules for an email (requires password for each or a session-less check).
- `GET /{id}`: Retrieve capsule details. Content is hidden if `ZonedDateTime.now() < openAt`.

### 3.4 Email Scheduler
- Runs periodically (e.g., every hour).
- Finds `LetterPaper` where `openAt <= now` and `isOpened == false`.
- Sends email and sets `isOpened = true`.

## 4. UI/UX Changes
- **Home:** Simplify to "Bury Capsule" and "Find My Capsule". Remove Login/Join.
- **Write:** Add Email and Password fields.
- **Success:** Show an animation of burying the capsule and display the "Bury Location" (Coordinates + Biome).
- **Find:** Simple form to enter email and password to see the list of capsules.

## 5. Security
- Remove JWT-based authentication.
- Access to capsule content is restricted by the capsule-specific password and the current time (open date).
