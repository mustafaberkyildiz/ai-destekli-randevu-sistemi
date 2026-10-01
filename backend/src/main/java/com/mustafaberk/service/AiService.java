package com.mustafaberk.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.mustafaberk.dto.DtoAIApiRecommendation;
import com.mustafaberk.dto.DtoAIResponse;
import com.mustafaberk.dto.DtoPythonRequest;
import com.mustafaberk.enums.Intention;
import com.mustafaberk.model.Department;
import com.mustafaberk.repository.DepartmentRepository;
import com.mustafaberk.service.impl.DepartmentServiceImpl;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

	@Value("${AI_SERVICE_URL:http://localhost:8080}")
	private String url ;

    private static final Logger logger = LoggerFactory.getLogger(AiService.class);
	
	private final ChatClient chatClient;
	
	@Autowired
	private DepartmentRepository departmentRepository;
	
	private final RestTemplate restTemplate = new RestTemplate();
	

	public DtoAIApiRecommendation getRecommendationFromPython(String symptomText) {

        DtoPythonRequest requestBody = new DtoPythonRequest(symptomText);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<DtoPythonRequest> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<DtoAIApiRecommendation> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        DtoAIApiRecommendation.class
                );

        return response.getBody();
    }
	
	public DtoAIResponse checkIntention(String message) {
		
		DtoAIResponse dtoAIResponse = new DtoAIResponse();
		dtoAIResponse.setDtoAIRecommendation(null);
		dtoAIResponse.setSuccess(false);
		dtoAIResponse.setMessage(null);
		
		String systemMessage = """	
			Sen bir metin sınıflandırma asistanısın. Görevin, kullanıcının girdiği metnin bir sağlık şikayeti veya tıbbi bir belirti olup olmadığını tespit etmektir.

			KURALLAR:
			1-Eğer metin bir hastalık, ağrı, semptom veya tıbbi bir durum içeriyorsa sadece 'HEALTH_ISSUE' yanıtını ver.
			
			2-Eğer metin selamlaşma (merhaba, nasılsın vb.) içeriyorsa sadece 'GREETING' yanıtını ver.
			
			3-Eğer metin bunların dışındaysa (ekonomi, hava durumu, genel bilgi soruları, anlamsız metinler vb.) sadece 'OTHER' yanıtını ver.
			
			4-Yanıtında asla açıklama yapma, cümle kurma. Sadece bu üç etiketten birini dön.
			""";
		
		String intentionString = chatClient.prompt().system(systemMessage).user(message.trim()).call().content();
				


	    return switch (intentionString) {
	        case "HEALTH_ISSUE" -> {
	        	DtoAIApiRecommendation dtoAIApiRecommendation = getRecommendationFromPython(message);
	        	dtoAIResponse.setSuccess(true);
	        	dtoAIResponse.setMessage(dtoAIApiRecommendation.getDepartment_name());
	        	yield dtoAIResponse;}
	        
	        case "GREETING" -> { 
	        	String returnMessage = "Merhaba! Ben yapay zeka sağlık asistanıyım. Lütfen randevu almak için şikayetinizi belirtin.";
	        	dtoAIResponse.setSuccess(false);
	        	dtoAIResponse.setMessage(returnMessage);
	        	yield dtoAIResponse;}
	        case "OTHER" -> {
	        	String returnMessage = "Üzgünüm, sadece sağlık şikayetleri ve randevu yönlendirmeleri konusunda yardımcı olabilirim.";
	        	dtoAIResponse.setSuccess(false);
	        	dtoAIResponse.setMessage(returnMessage);
	        	yield dtoAIResponse;}
	        default -> {
	            logger.warn("AI garip bir yanıt verdi: {}", intentionString);
	            String returnMessage = "Üzgünüm, isteğinizi tam olarak sınıflandıramadım. Lütfen sağlık şikayetinizi net bir şekilde yazar mısınız?";
	        	dtoAIResponse.setSuccess(false);
	        	dtoAIResponse.setMessage(returnMessage);
	            yield dtoAIResponse;

	        }
	    };
	}
	
}
