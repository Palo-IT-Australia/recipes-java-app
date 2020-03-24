package au.com.imed.portal.referrer.referrerportal.common.util;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

import org.apache.commons.lang3.StringUtils;

@Converter
public class BooleanToStringConverter implements AttributeConverter<Boolean, String> {

	@Override
	public String convertToDatabaseColumn(Boolean value) {
		return (value != null && value) ? "Yes" : "No";
	}

	@Override
	public Boolean convertToEntityAttribute(String value) {
		if (StringUtils.isEmpty(value)) {
			return null;
		} else {
			return value.equalsIgnoreCase("yes") ? true : false;
		}
	}
}
