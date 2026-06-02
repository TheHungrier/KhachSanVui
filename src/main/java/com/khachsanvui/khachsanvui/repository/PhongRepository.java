package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.Phong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PhongRepository extends JpaRepository<Phong, Integer> {
    // Kế thừa JpaRepository sẽ tự động cung cấp hàm findById(Integer id)
}