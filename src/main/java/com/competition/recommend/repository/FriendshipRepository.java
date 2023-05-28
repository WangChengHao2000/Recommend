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

    Optional<Friendship> findByUserIdAndFriendId(Long userId, Long friendId);

    @Transactional
    void deleteFriendshipByUserIdAndFriendId(Long userId, Long friendId);
}
