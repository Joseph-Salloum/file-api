package com.joseph.fileapi.model.constant;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

public enum ApiKeyStatus {
    ACTIVE,
    INACTIVE;

    @Converter (autoApply = true)
    public static class Mapper implements AttributeConverter<ApiKeyStatus, String> {
        @Override
        public String convertToDatabaseColumn(ApiKeyStatus attribute) {
            return ((attribute == null) ? null : attribute.name().toLowerCase());
        }

        @Override
        public ApiKeyStatus convertToEntityAttribute(String dbData) {
            return ((dbData == null) ? null : ApiKeyStatus.valueOf(dbData.toUpperCase()));
        }
    }
}
