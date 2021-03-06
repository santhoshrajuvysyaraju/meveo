package org.meveo.model.crm.custom;

public enum CustomFieldMapKeyEnum {
    STRING("customFieldMapKeyTypeEnum.STRING"), RON("customFieldMapKeyTypeEnum.RON");

    private String label;

    CustomFieldMapKeyEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}