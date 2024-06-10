package com.bikeit.restendpoint.repository;

import com.bikeit.restendpoint.model.Map;
import com.bikeit.restendpoint.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MapRepository extends JpaRepository<Map, Long> {
    List<Map> findByUser(User user);
}
