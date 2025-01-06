package com.likelion.JoinUP.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecruitPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recruitpost_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String location;

    @Column(nullable = false)
    private int maxMembers;

    @Column(nullable = false)
    private int currentMembers;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User writer;

    @ManyToMany
    @JoinTable(
            name = "recruit_post_queue",
            joinColumns = @JoinColumn(name = "recruitpost_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> queuedUsers = new ArrayList<>();
}
