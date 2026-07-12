package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.model.DiaDiemDL;
import com.khachsanvui.khachsanvui.repository.DiaDiemDLRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DiaDiemDLService {

    private final DiaDiemDLRepository diaDiemDLRepository;

    public DiaDiemDLService(DiaDiemDLRepository diaDiemDLRepository) {
        this.diaDiemDLRepository = diaDiemDLRepository;
    }

    public List<DiaDiemDL> findAll() {
        return diaDiemDLRepository.findAll();
    }
}