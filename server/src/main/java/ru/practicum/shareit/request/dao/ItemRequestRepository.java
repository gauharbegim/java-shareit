package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.List;


@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Integer> {
    List<ItemRequest> findByRequestor(User user);
    Page<ItemRequest> findAllByRequestorIdNot(Integer userId, Pageable page);

//    @Query(nativeQuery = true, value = "SELECT * from (SELECT b.*, ROWNUM() r FROM (SELECT b.* FROM REQUESTS b WHERE b.USER_ID<>:user ORDER BY DATE_CREATE DESC ) b ) " +
//            "WHERE r>:from and ROWNUM<=:size ORDER BY DATE_CREATE DESC")
//    List<ItemRequest> findByRequestorLimits(@Param("user") Integer userId, @Param("from") Integer from, @Param("size") Integer size);

    Page<ItemRequest> findAllByRequestor(User requestor, Pageable page);
}
