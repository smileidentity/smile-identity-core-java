package smile.identity.core.adapters;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.HashMap;
import java.util.Map;

import smile.identity.core.enums.JobType;
import smile.identity.core.models.PartnerParams;

/**
 *  Our API allows users to send optional fields within the partnerParams body.
 *  This adapter handles adding the values in the optionalInfo map as top level keys
 *  of the partnerParams body. It also converts those optional fields back when received
 *  from the server.
 */
public class PartnerParamsAdapter {

    @FromJson PartnerParams fromJson(Map<String, Object> params){
        String jobId = (String) params.remove("job_id");
        String userId = (String) params.remove("user_id");
        JobType jobType = getJobType(params.remove("job_type"));

        return new PartnerParams(jobType, userId, jobId, params);
    }

    @ToJson
    Map<String, Object> toJson(PartnerParams partnerParams){
        Map<String, Object> map = new HashMap<>();
        map.put("user_id", partnerParams.getUserId());
        map.put("job_id", partnerParams.getJobId());
        map.put("job_type", partnerParams.getJobType().getValue());
        for(Map.Entry<String, Object> entry : partnerParams.getOptionalInfo().entrySet()){
            map.put(entry.getKey(), entry.getValue());
        }

        return map;
    }

    private JobType getJobType(Object value){
        if (value instanceof Double) {
            return JobType.fromValue(((Double) value).intValue());
        }
        return JobType.fromValue(Integer.parseInt((String) value));
    }
}
