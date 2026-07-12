package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.LoaiPhong;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoaiPhongRepository extends JpaRepository<LoaiPhong, Integer>, JpaSpecificationExecutor<LoaiPhong> {

    List<LoaiPhong> findAllByOrderByMaLoaiPhongDesc();
}