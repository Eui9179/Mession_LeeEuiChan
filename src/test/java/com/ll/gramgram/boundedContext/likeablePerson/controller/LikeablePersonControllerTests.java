package com.ll.gramgram.boundedContext.likeablePerson.controller;


import com.ll.gramgram.base.appConfig.AppConfig;
import com.ll.gramgram.boundedContext.likeablePerson.entity.LikeablePerson;
import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
public class LikeablePersonControllerTests {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private LikeablePersonService likeablePersonService;
    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("등록 폼")
    @WithUserDetails("user1")
    void t001() throws Exception {
        //when
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/add"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showAdd"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("먼저 본인의 인스타그램")));
    }

    @Test
    @DisplayName("등록 폼")
    @WithUserDetails("user2")
    void t002() throws Exception {
        //when
        ResultActions resultActions = mvc
                .perform(get("/likeablePerson/add"))
                .andDo(print());

        //then
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("showAdd"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(containsString("당신의 인스타ID")));
    }

    @Test
    @DisplayName("등록 폼 처리(user2가 user3에게 호감 표시)")
    @WithUserDetails("user2")
    void t003() throws Exception {
        //when
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/add")
                        .with(csrf())
                        .param("username", "insta_user3")
                        .param("attractiveTypeCode", "1"))
                .andDo(print());

        //then
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/likeablePerson/list**"));

    }

    @Test
    @DisplayName("호감삭제")
    @WithUserDetails("user3")
    void t005() throws Exception {
        //when
        ResultActions resultActions = mvc
                .perform(delete("/likeablePerson/1")
                        .with(csrf()))
                .andDo(print());

        //then
        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/likeablePerson/list**"));

        assertThat(likeablePersonService.findById(1L).isPresent()).isFalse();

    }

    @Test
    @DisplayName("호감 삭제(권한이 없는 경우)")
    @WithUserDetails("user2")
    void t006() throws Exception {
        ResultActions resultActions = mvc
                .perform(delete("/likeablePerson/1")
                        .with(csrf()))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("delete"))
                .andExpect(status().is4xxClientError()); //history back -> 400 error
    }

    @Test
    @DisplayName("호감 표시 아이디와 매력 포인트 중복")
    @WithUserDetails("user3")
    void t007() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/add")
                        .with(csrf())
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", "1"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("add"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("호감 표시 아이디 중복 & 매력 포인트 변경")
    @WithUserDetails("user3")
    void t008() throws Exception {
        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/add")
                        .with(csrf())
                        .param("username", "insta_user4")
                        .param("attractiveTypeCode", "2"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("add"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/likeablePerson/list**"));

        Optional<Member> member = memberService.findByUsername("user3");
        assertTrue(member.isPresent());

        Optional<LikeablePerson> likeablePerson = likeablePersonService.findLikeablePersonOne(member.get().getInstaMember(), "insta_user4");
        assertTrue(likeablePerson.isPresent());
        assertEquals(likeablePerson.get().getAttractiveTypeCode(), 2);
    }

    @Test
    @DisplayName("10개 이상 등록 시 오류")
    @WithUserDetails("user2")
    void t009() throws Exception {
        Member member = memberService.findByUsername("user2")
                .orElseThrow();

        for (int i = 0; i < AppConfig.getLikeablePersonFromMax(); i++) {
            likeablePersonService.like(member, "test_insta_" + i, 1);
        }

        ResultActions resultActions = mvc
                .perform(post("/likeablePerson/add")
                        .with(csrf())
                        .param("username", "test_insta_11")
                        .param("attractiveTypeCode", "1"))
                .andDo(print());

        resultActions
                .andExpect(handler().handlerType(LikeablePersonController.class))
                .andExpect(handler().methodName("add"))
                .andExpect(status().is4xxClientError())
        ;
    }
}
