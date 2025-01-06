package com.likelion.JoinUP.repository;

import com.likelion.JoinUP.entity.Comment;
import com.likelion.JoinUP.entity.CommunityPost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
