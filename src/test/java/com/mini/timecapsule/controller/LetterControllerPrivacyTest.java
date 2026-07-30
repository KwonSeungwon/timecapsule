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

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    /**
     * 개봉일 전에 올바른 비밀번호로 열면 <b>200 + {@code openable:false} + 본문 없음</b>이다.
     *
     * <p>이게 프론트가 소비하는 계약이다. 예전엔 이 자리에
     * "403 + 아직 개봉할 수 없는 타임캡슐입니다. D-N" 이 나간다고 <i>믿어졌지만</i>
     * {@code LetterService#getDetail} 이 개봉일 전이면 그냥 반환하기 때문에 사실이
     * 아니었다(그 믿음 때문에 임의의 {@code IllegalStateException} 메시지를 그대로
     * 내보내는 핸들러가 남아 있었다).
     *
     * <p>여기서 진짜로 확인해야 하는 건 <b>200 을 주면서 본문까지 같이 주고 있지는 않은가</b>
     * 다. 만약 content 가 실려 나간다면 개봉일은 UI 에서만 지켜지는 장식이 되고,
     * 개발자도구 Network 탭만 열면 캡슐을 미리 읽을 수 있다.
     */
    @Test
    void lockedCapsuleReturns200WithoutContentEvenWithTheRightPassword() throws Exception {
        letterService.sendLetter(capsule()
                .title("아직잠긴캡슐").receiverEmail("locked@example.com")
                .openAt(ZonedDateTime.now().plusDays(7)).isPublic(false).build());

        String id = mockMvc.perform(get("/api/v1/capsule/list").param("email", "locked@example.com"))
                .andReturn().getResponse().getContentAsString()
                .replaceAll("^.*?\"id\":(\\d+).*$", "$1");

        mockMvc.perform(post("/api/v1/capsule/" + id + "/open")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"password\":\"pw\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.openable").value(false))
                .andExpect(jsonPath("$.dday").value(7))
                // 본문·이미지는 개봉 전까지 절대 나가면 안 된다.
                .andExpect(jsonPath("$.content").doesNotExist())
                .andExpect(jsonPath("$.imageUrl").doesNotExist());
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
