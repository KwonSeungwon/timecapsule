package com.mini.timecapsule.model;

import org.junit.jupiter.api.Test;
import java.time.ZonedDateTime;
import static org.junit.jupiter.api.Assertions.*;

class LetterPaperTest {

    @Test
    void testNewEntity() {
        String senderName = "Sender";
        String title = "Title";
        String receiverEmail = "receiver@example.com";
        String password = "password";
        Integer xCoord = 10;
        Integer yCoord = 20;
        String biome = "BEACH";
        String locationName = "Haeundae";
        LetterPaper.LetterPaperType letterPaperType = LetterPaper.LetterPaperType.LETTER;
        LetterPaper.CapsuleType capsuleType = LetterPaper.CapsuleType.BOTTLE;
        String content = "Hello";
        ZonedDateTime openAt = ZonedDateTime.now().plusDays(7);

        LetterPaper letterPaper = LetterPaper.newEntity(
                senderName, title, receiverEmail, password, xCoord, yCoord, biome, locationName,
                letterPaperType, capsuleType, content, null, null, openAt, false
        );

        assertEquals(senderName, letterPaper.getSenderName());
        assertEquals(title, letterPaper.getTitle());
        assertEquals(receiverEmail, letterPaper.getReceiverEmail());
        assertEquals(password, letterPaper.getPassword());
        assertEquals(xCoord, letterPaper.getXCoord());
        assertEquals(yCoord, letterPaper.getYCoord());
        assertEquals(biome, letterPaper.getBiome());
        assertEquals(locationName, letterPaper.getLocationName());
        assertEquals(LetterPaper.LetterStatus.UNOPENED, letterPaper.getStatus());
    }
}
