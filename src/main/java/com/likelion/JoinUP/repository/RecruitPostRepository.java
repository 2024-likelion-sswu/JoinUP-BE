package com.likelion.JoinUP.repository;

import com.likelion.JoinUP.entity.RecruitPost;
import com.likelion.JoinUP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecruitPostRepository extends JpaRepository<RecruitPost, Long> {



    List<RecruitPost> findByLocationContaining(String location);

}
