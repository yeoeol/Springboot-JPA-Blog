package com.cos.blog.controller;

import com.cos.blog.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping("/")
    public String index(Model model, @PageableDefault(size = 3, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        model.addAttribute("boards", boardService.list(pageable));
        return "index";
    }

    @GetMapping("/board/{id}")
    public String detail(@PathVariable int id, Model model) {
        System.out.println("BoardController.detail");
        model.addAttribute("board", boardService.detail(id));
        return "board/detail";
    }

    @GetMapping("/board/{id}/update")
    public String updateForm(@PathVariable int id, Model model) {
        model.addAttribute("board", boardService.detail(id));
        return "board/updateForm";
    }

    // USER 권한이 필요
    @GetMapping("/board/save")
    public String saveForm() {
        return "board/saveForm";
    }
}
