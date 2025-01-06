package com.likelion.JoinUP.repository;

import com.likelion.JoinUP.entity.CommunityPost;
import com.likelion.JoinUP.entity.CommunityPostLike;
import com.likelion.JoinUP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<CommunityPostLike, Long> {
    boolean existsByPostAndUser(CommunityPost post, User user);
}
