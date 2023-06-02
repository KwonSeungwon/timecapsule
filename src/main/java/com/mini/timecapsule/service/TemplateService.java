package com.mini.timecapsule.service;

import com.mini.timecapsule.dao.TemplateRepository;
import com.mini.timecapsule.dto.TemplateDto;
import com.mini.timecapsule.exception.CustomException;
import com.mini.timecapsule.exception.ExceptionStructure;
import com.mini.timecapsule.model.QTemplate;
import com.mini.timecapsule.model.Template;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TemplateService {

    private final TemplateRepository templateRepository;

    public final Iterable<Template> list(TemplateDto templateDto) {

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

    public Template findOne(TemplateDto templateDto) {

        Optional<Template> template = templateRepository.findById(templateDto.getId());

        return template.orElse(null);
    }

    public void create(TemplateDto templateDto) {
        Template template = Template.newTemplate(templateDto.getTemplateType(), templateDto.getName(), templateDto.getExplain(), templateDto.getUrl(), Template.TemplateStatus.ACTIVE);
        templateRepository.save(template);
    }

    public void delete(Long id) {
        templateRepository.deleteById(id);
    }

    public void update(TemplateDto templateDto) {
        Optional<Template> template = templateRepository.findById(templateDto.getId());
        if (!template.isPresent()) {
            throw new CustomException(ExceptionStructure.NOT_FOUND_TEMPLATE);
        } else {
            Template templateObj = template.get();
            templateObj.updateTemplateInfo(templateDto.getExplain(), templateDto.getUrl());
        }
    }

    public void unChangeStatus(Long id) {
        Optional<Template> template = templateRepository.findById(id);
        if (!template.isPresent()) {
            throw new CustomException(ExceptionStructure.NOT_FOUND_TEMPLATE);
        } else {
            Template templateObj = template.get();
            if (templateObj.getTemplateStatus().equals(Template.TemplateStatus.ACTIVE)) {
                templateObj.changeStatus(Template.TemplateStatus.INACTIVE);
            } else {
                templateObj.changeStatus(Template.TemplateStatus.ACTIVE);
            }
        }
    }








}
