package com.mini.timecapsule.controller;

import com.mini.timecapsule.dto.CapsuleCreateRequest;
import com.mini.timecapsule.model.LetterPaper;
import com.mini.timecapsule.service.LetterService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * /api/v1/capsule/public 은 인증 없이 누구나 호출 가능한 엔드포인트다.
 * 여기에 수신자 이메일이나 본문이 실려 나가면 그대로 수집당한다.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LetterControllerPrivacyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LetterService letterService;

    private CapsuleCreateRequest.CapsuleCreateRequestBuilder capsule() {
        return CapsuleCreateRequest.builder()
                .senderName("보낸이")
                .password("pw")
                .content("내용입니다")
                .openAt(ZonedDateTime.now().plusDays(1))
                .letterPaperType(LetterPaper.LetterPaperType.LETTER)
                .capsuleType(LetterPaper.CapsuleType.BOTTLE);
    }

    @Test
    void publicListingDoesNotLeakReceiverEmailOrContent() throws Exception {
        letterService.sendLetter(capsule()
                .title("공개캡슐").receiverEmail("secret@example.com").isPublic(true).build());

        mockMvc.perform(get("/api/v1/capsule/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title=='공개캡슐')]").exists())
                // 목록 전체를 훑는다 — 한 건이라도 이메일/본문이 실리면 실패한다.
                .andExpect(jsonPath("$..receiverEmail").doesNotExist())
                .andExpect(jsonPath("$..content").doesNotExist())
                .andExpect(jsonPath("$..password").doesNotExist())
                .andExpect(jsonPath("$..requestorInfo").doesNotExist());
    }

    @Test
    void publicListingStillCarriesTheFieldsExploreNeeds() throws Exception {
        letterService.sendLetter(capsule()
                .title("탐색용").receiverEmail("explore@example.com").isPublic(true).build());

        mockMvc.perform(get("/api/v1/capsule/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title=='탐색용')].biome").exists())
                .andExpect(jsonPath("$[?(@.title=='탐색용')].locationName").exists())
                .andExpect(jsonPath("$[?(@.title=='탐색용')].xCoord").exists())
                .andExpect(jsonPath("$[?(@.title=='탐색용')].yCoord").exists())
                .andExpect(jsonPath("$[?(@.title=='탐색용')].senderName").exists());
    }

    @Test
    void privateCapsuleNeverAppearsInThePublicListing() throws Exception {
        letterService.sendLetter(capsule()
                .title("비공개캡슐").receiverEmail("private@example.com").isPublic(false).build());

        mockMvc.perform(get("/api/v1/capsule/public"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[?(@.title=='비공개캡슐')]").doesNotExist());
    }

    @Test
    void ownerListingStillIncludesReceiverEmail() throws Exception {
        letterService.sendLetter(capsule()
                .title("내캡슐").receiverEmail("owner-privacy@example.com").isPublic(false).build());

        mockMvc.perform(get("/api/v1/capsule/list").param("email", "owner-privacy@example.com"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].receiverEmail").value("owner-privacy@example.com"));
    }
}
