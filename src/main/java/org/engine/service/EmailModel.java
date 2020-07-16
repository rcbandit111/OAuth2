package org.engine.service;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EmailModel {
	
	private String mailFrom;

	private String mailTo;
	
	private String mailSubject;
	
	private String mailContent;
	
	private Map<String, Object> model;
}
