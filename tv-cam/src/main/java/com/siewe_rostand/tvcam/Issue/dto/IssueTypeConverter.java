package com.siewe_rostand.tvcam.Issue.dto;

import com.siewe_rostand.tvcam.Issue.model.enumeration.IssueType;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = false)
public class IssueTypeConverter implements AttributeConverter<IssueType, String> {

    @Override
    // Logic: IssueType (Java) -> String (Database)
    public String convertToDatabaseColumn(IssueType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDescription();
    }

    @Override
    public IssueType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return IssueType.fromDescription(dbData);
    }
}
