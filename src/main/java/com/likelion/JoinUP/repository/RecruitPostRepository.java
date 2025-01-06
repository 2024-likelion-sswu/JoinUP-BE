package com.likelion.JoinUP.repository;

import com.likelion.JoinUP.entity.RecruitPost;
import com.likelion.JoinUP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long> {
    // 사용자가 작성한 모집글 조회
    List<RecruitPost> findByWriter(User writer);

    // 사용자가 줄서 있는 모집글 조회
    List<RecruitPost> findByQueuedUsersContaining(User user);

    List<RecruitPost> findByLocationContaining(String location);
}
