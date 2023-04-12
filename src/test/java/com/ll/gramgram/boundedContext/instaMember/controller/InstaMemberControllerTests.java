package com.ll.gramgram.boundedContext.instaMember.controller;

import com.ll.gramgram.base.exception.DataNotFoundException;
import com.ll.gramgram.boundedContext.instaMember.entity.InstaMember;
import com.ll.gramgram.boundedContext.instaMember.service.InstaMemberService;
import com.ll.gramgram.boundedContext.member.entity.Member;
import com.ll.gramgram.boundedContext.member.service.MemberService;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc // http 요청, 응답 테스트
@Transactional
@ActiveProfiles("test")
public class InstaMemberControllerTests {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private InstaMemberService instaMemberService;

    @Autowired
    private MemberService memberService;

    @Test
    @DisplayName("인스타회원 정보 입력 폼")
    @WithUserDetails("user1")
    void t001() throws Exception {
        //when
        ResultActions resultActions = mvc
                .perform(MockMvcRequestBuilders.get("/instaMember/connect"))
                .andDo(print());

        //then
        resultActions
                .andExpect(handler().handlerType(InstaMemberController.class))
                .andExpect(handler().methodName("showConnect"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(content().string(Matchers.containsString("""
                        <input type="text" name="username"
                        """.stripIndent().trim())));
    }

    @Test
    @DisplayName("로그인을 안하고 인스타회원 정보 입력 페이지에 접근하면 로그인 페이지로 302")
    void t002() throws Exception {
        //when
        ResultActions resultActions = mvc
                .perform(MockMvcRequestBuilders.get("/instaMember/connect"))
                .andDo(print());

        //then
        resultActions
                .andExpect(handler().handlerType(InstaMemberController.class))
                .andExpect(handler().methodName("showConnect"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/member/login**"));
    }

    @Test
    @DisplayName("인스타회원 정보 입력 폼 처리")
    @WithUserDetails("user1")
    void t003() throws Exception {
        // WHEN
        ResultActions resultActions = mvc
                .perform(post("/instaMember/connect")
                        .with(csrf()) // CSRF 키 생성
                        .param("username", "abc123")
                        .param("gender", "W")
                )
                .andDo(print());

        // THEN
        resultActions
                .andExpect(handler().handlerType(InstaMemberController.class))
                .andExpect(handler().methodName("connect"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/likeablePerson/add**"));

        InstaMember instaMember = instaMemberService.findByUsername("abc123").orElse(null);

        Member member = memberService.findByUsername("user1").orElseThrow();
        assertThat(member.getInstaMember()).isEqualTo(instaMember);
    }

    @Test
    @DisplayName("인스타 아이디 입력, 이미 우리 시스템에 성별 U로 등록되어 있는 경우")
    @WithUserDetails("user1")
    void t004() throws Exception {
        //when
        ResultActions resultActions = mvc
                .perform(post("/instaMember/connect")
                        .with(csrf())
                        .param("username", "insta_user_test004")
                        .param("gender", "M"))
                .andDo(print());

        //then
        resultActions
                .andExpect(handler().handlerType(InstaMemberController.class))
                .andExpect(handler().methodName("connect"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/likeablePerson/add**"));

        InstaMember instaMember = instaMemberService.findByUsername("insta_user_test004").orElse(null);

        assertThat(instaMember).isNotNull();
        assertThat(instaMember.getGender()).isEqualTo("M");

        Member member = memberService.findByUsername("user1").orElse(null);
        assertThat(member).isNotNull();
        assertThat(member.getInstaMember()).isEqualTo(instaMember);
    }
}
