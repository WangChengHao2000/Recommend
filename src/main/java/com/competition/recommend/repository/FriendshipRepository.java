package com.competition.recommend.repository;

import com.competition.recommend.entity.Friendship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship,Long> {

    List<Friendship> findAllByUserId(Long userId);

    Page<Friendship> findAllByUserId(Long userId, PageRequest pageRequest);

    Optional<Friendship> findByUserIdAndFriendName(Long userId, String friendName);
    @Transactional
    void deleteFriendshipsByFriendName(String friendName);

    @Transactional
    void deleteFriendshipByUserIdAndFriendName(Long userId, String friendName);
}
