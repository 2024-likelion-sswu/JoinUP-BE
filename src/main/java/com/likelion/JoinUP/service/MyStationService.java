package com.likelion.JoinUP.service;

import com.likelion.JoinUP.dto.MyStationDTO;
import com.likelion.JoinUP.entity.MyStation;
import com.likelion.JoinUP.entity.User;
import com.likelion.JoinUP.repository.MyStationRepository;
import com.likelion.JoinUP.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MyStationService {

    private final MyStationRepository myStationRepository;
    private final UserRepository userRepository;

    public MyStationService(MyStationRepository myStationRepository, UserRepository userRepository) {
        this.myStationRepository = myStationRepository;
        this.userRepository = userRepository;
    }

    // 나의 정류장 조회
    public List<MyStationDTO.MyStationResponse> getMyStations(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        List<MyStation> stations = myStationRepository.findByUser(user);
        return stations.stream()
                .map(station -> new MyStationDTO.MyStationResponse(station.getId(), station.getStationName()))
                .collect(Collectors.toList());
    }

    // 정류장 추가
    public String addMyStation(String email, MyStationDTO.AddMyStationRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        // 중복 추가 방지
        if (myStationRepository.findByUserAndStationName(user, request.getStationName()).isPresent()) {
            return "중복된 정류장입니다.";
        }

        MyStation myStation = MyStation.builder()
                .stationName(request.getStationName())
                .user(user)
                .build();

        myStationRepository.save(myStation);
        return "나의 정류장에 추가되었습니다.";
    }

    // 정류장 삭제
    public void deleteMyStation(String email, Long id) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        MyStation myStation = myStationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 정류장입니다."));

        // 해당 사용자가 소유한 정류장인지 확인
        if (!myStation.getUser().equals(user)) {
            throw new RuntimeException("정류장을 삭제할 권한이 없습니다.");
        }

        myStationRepository.delete(myStation);
    }
}
