package com.khachsanvui.khachsanvui.repository;

import com.khachsanvui.khachsanvui.model.DiaDiemDL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DiaDiemDLRepository extends JpaRepository<DiaDiemDL, Integer> {
}