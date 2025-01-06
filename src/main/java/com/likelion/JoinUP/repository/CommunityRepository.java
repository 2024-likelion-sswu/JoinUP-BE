package com.likelion.JoinUP.repository;

import com.likelion.JoinUP.entity.CommunityPost;
import com.likelion.JoinUP.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommunityRepository extends JpaRepository<CommunityPost, Long> {
    List<CommunityPost> findByCategory(CommunityPost.Category category);

    List<CommunityPost> findByLikesGreaterThanEqual(int likes);
}