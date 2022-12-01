package smile.identity.core;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.wnameless.json.flattener.FlattenMode;
import com.github.wnameless.json.flattener.JsonFlattener;

import org.apache.commons.text.CaseUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import smile.identity.core.exceptions.IdTypeNotSupported;
import smile.identity.core.exceptions.MissingRequiredFields;
import smile.identity.core.models.IdInfo;
import smile.identity.core.models.PartnerParams;

public class IdValidator {


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
        ObjectMapper mapper = new ObjectMapper();
        String country = idInfo.getCountry();
        String idType = idInfo.getIdType();

        List<String> requiredFields = getRequiredFields(service, idType,
                country);

        if (requiredFields.isEmpty()) {
            throw new IdTypeNotSupported(country, idType);
        }

        JsonNode idInfoNode = mapper.convertValue(idInfo, JsonNode.class);
        JsonNode partnerParamsNode = mapper.convertValue(partnerParams,
                JsonNode.class);

        for (String field : requiredFields) {
            String camelCase = CaseUtils.toCamelCase(field, false, '_');

            if (idInfoNode.get(camelCase) == null && partnerParamsNode.get(camelCase) == null) {
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
        Map<String, Object> flattened =
                new JsonFlattener(services).withFlattenMode(FlattenMode.KEEP_PRIMITIVE_ARRAYS).flattenAsMap();

        String search = String.join(".", "id_types", country, idType);
        return (List<String>) flattened.getOrDefault(search, new ArrayList<>());
    }

}
