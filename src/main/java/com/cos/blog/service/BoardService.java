package com.cos.blog.service;

import com.cos.blog.model.Board;
import com.cos.blog.model.Reply;
import com.cos.blog.model.User;
import com.cos.blog.repository.BoardRepository;
import com.cos.blog.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//스프링이 컴포넌트 스캔을 통해서 Bean에 등록해줌. IoC를 해줌
@Service
@RequiredArgsConstructor
@Transactional
public class BoardService {

    public final BoardRepository boardRepository;
    public final ReplyRepository replyRepository;

    public void write(Board board, User user) {    //title, content
        board.setCount(0);
        board.setUser(user);
        boardRepository.save(board);
    }


    @Transactional(readOnly = true)
    public Page<Board> list(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Board detail(int id) {
        return boardRepository.findById(id).orElseThrow(
                        () -> new IllegalArgumentException("글 상세보기 실패: 아이디를 찾을 수 없습니다.")
                );
    }

    public void delete(int id) {
        System.out.println("글 삭제하기: " + id);
        boardRepository.deleteById(id);
    }

    public void update(int id, Board requestBoard) {
        Board board = boardRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalArgumentException("글 찾기 실패: 아이디를 찾을 수 없습니다.")
                );  // 영속화 완료
        board.setTitle(requestBoard.getTitle());
        board.setContent(requestBoard.getContent());
        // 해당 함수 종료 시(service가 종료될 때) 트랜잭션이 종료됨 -> 더티체킹 - 자동 업데이트, db flush
    }

    public void commentWrite(User user, int boardId, Reply requestReply) {
        Board board = boardRepository.findById(boardId)
                .orElseThrow(
                        () -> new IllegalArgumentException("댓글 쓰기 실패 : 게시글 id를 찾을 수 없습니다.")
                );  // 영속화 완료

        requestReply.setUser(user);
        requestReply.setBoard(board);

        replyRepository.save(requestReply);
    }
}
