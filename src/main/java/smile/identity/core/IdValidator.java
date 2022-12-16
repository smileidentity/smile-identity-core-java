package smile.identity.core;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CaseFormat;


import java.util.ArrayList;
import java.util.List;

import smile.identity.core.exceptions.IdTypeNotSupported;
import smile.identity.core.exceptions.MissingRequiredFields;
import smile.identity.core.models.IdInfo;
import smile.identity.core.models.PartnerParams;

public class IdValidator {

    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * Validates that the correct fields are provided for a id type based on
     * country
     *
     * @param service       SmileIdentityService
     * @param idInfo        IdInfo
     * @param partnerParams PartnerParams
     * @throws Exception throws an exception if it cannot access /services
     *                   endpoint
     */
    public static void validateIdType(SmileIdentityService service,
                                      IdInfo idInfo,
                                      PartnerParams partnerParams) throws Exception {
        String country = idInfo.getCountry();
        String idType = idInfo.getIdType();

        List<String> requiredFields = getRequiredFields(service, idType,
                country);

        if (requiredFields.isEmpty()) {
            throw new IdTypeNotSupported(country, idType);
        }

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        JsonNode idInfoNode = mapper.convertValue(idInfo, JsonNode.class);
        JsonNode partnerParamsNode = mapper.convertValue(partnerParams,
                JsonNode.class);

        for (String field : requiredFields) {
            String camelCase =
                    CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL,
                            field);

            if ((idInfoNode.get(camelCase) == null) && partnerParamsNode.get(camelCase) == null) {
                throw new MissingRequiredFields(field);
            }
        }
    }

    /***
     *  Returns the required fields for a ID Type based on country
     * @param service SmileIdentityService
     * @param idType type of id
     * @param country country name
     * @return A list of required fields as Strings
     * @throws Exception throws an exception if it cannot access /services
     * endpoint
     */
    @SuppressWarnings("unchecked")
    public static List<String> getRequiredFields(SmileIdentityService service
            , String idType, String country) throws Exception {

        String services = service.getServices();

        JsonNode rootNode = mapper.readTree(services);
        JsonNode requiredFields =
                rootNode.path("id_types").path(country).path(idType);
        if ( requiredFields.isMissingNode()) {
            return new ArrayList<>();
        }
        return mapper.reader(new TypeReference<List<String>>(){}).readValue(requiredFields);
    }

}
