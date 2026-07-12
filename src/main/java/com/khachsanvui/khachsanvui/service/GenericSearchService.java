package com.khachsanvui.khachsanvui.service;

import com.khachsanvui.khachsanvui.dto.PageResponseDTO;
import com.khachsanvui.khachsanvui.repository.specification.SearchSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenericSearchService {

    public <T> PageResponseDTO<T> search(JpaSpecificationExecutor<T> repository,
                                         String keyword,
                                         List<String> searchFields,
                                         String sortBy,
                                         int page,
                                         int size,
                                         String idFieldName) {

        Specification<T> spec = SearchSpecification.buildSpec(keyword, searchFields);
        Pageable pageable = SearchSpecification.buildPageable(page, size, sortBy, idFieldName);

        Page<T> pageResult = repository.findAll(spec, pageable);
        return new PageResponseDTO<>(pageResult);
    }
}