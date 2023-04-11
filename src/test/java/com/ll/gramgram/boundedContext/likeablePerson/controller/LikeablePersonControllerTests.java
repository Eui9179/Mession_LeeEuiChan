package com.ll.gramgram.boundedContext.likeablePerson.controller;


import com.ll.gramgram.boundedContext.likeablePerson.service.LikeablePersonService;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
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
}
