package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.TemplateRepository;
import com.mini.timecapsule.dto.TemplateDto;
import com.mini.timecapsule.model.QTemplate;
import com.mini.timecapsule.model.Template;
import com.querydsl.core.BooleanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    public Iterable<Template> list(TemplateDto templateDto) {

        QTemplate qTemplate = QTemplate.template;
        BooleanBuilder predicate = new BooleanBuilder();

        if (templateDto.getId() != null) {
            predicate.and(qTemplate.id.eq(templateDto.getId()));
        }
        if (templateDto.getTemplateStatus() != null) {
            predicate.and(qTemplate.templateStatus.eq(templateDto.getTemplateStatus()));
        }
        if (templateDto.getTemplateType() != null) {
            predicate.and(qTemplate.type.eq(templateDto.getTemplateType()));
        }
        if (templateDto.getName() != null) {
            predicate.and(qTemplate.name.contains(templateDto.getName()));
        }
        return templateRepository.findAll(predicate);
    }
}
