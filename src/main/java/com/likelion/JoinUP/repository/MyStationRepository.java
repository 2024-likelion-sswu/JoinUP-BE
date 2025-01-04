package com.likelion.JoinUP.repository;

import com.likelion.JoinUP.entity.MyStation;
import com.likelion.JoinUP.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MyStationRepository extends JpaRepository<MyStation, Long> {
    List<MyStation> findByUser(User user);

    Optional<MyStation> findByUserAndStationName(User user, String stationName); // 특정 사용자와 정류장 이름으로 조회
}
