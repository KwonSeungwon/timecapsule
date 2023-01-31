package com.mini.timecapsule.dto;

import com.mini.timecapsule.model.Template;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TemplateDto {

    private Long id;

    private String name;

    private String explain;

    private String url;

    private Template.TemplateType templateType;

    private Template.TemplateStatus templateStatus;

}
