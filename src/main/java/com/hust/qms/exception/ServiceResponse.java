package com.hust.qms.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ServiceResponse {

        private String status;
        private int statusCode;
        private String message;
        private Object data;

    public static ServiceResponse RESPONSE_MESSAGES (String status, int statusCode, String message) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus(status);
        serviceResponse.setStatusCode(statusCode);
        serviceResponse.setMessage(message);
        serviceResponse.setData(null);
        return serviceResponse;
    }

    public static ServiceResponse SUCCESS_RESPONSE(String message, Object data) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus("SUCCESS");
        serviceResponse.setStatusCode(HttpStatus.SC_OK);
        serviceResponse.setMessage(message);
        serviceResponse.setData(data);
        return serviceResponse;
    }

    public static ServiceResponse BAD_RESPONSE(String message) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus("Bad request!");
        serviceResponse.setStatusCode(HttpStatus.SC_BAD_REQUEST);
        serviceResponse.setMessage(message);
        return serviceResponse;
    }

    public static ServiceResponse FORBIDDEN_RESPONSE(String message) {
        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setStatus("Request forbidden!");
        serviceResponse.setStatusCode(HttpStatus.SC_FORBIDDEN);
        serviceResponse.setMessage(message);
        return serviceResponse;
    }


}
