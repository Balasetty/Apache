package com.example.demo;

import org.apache.camel.ValidationException;
import com.fasterxml.jackson.core.JsonParseException;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestParamType;
import org.springframework.stereotype.Component;
import org.apache.camel.model.dataformat.JsonLibrary;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;

@Component
public class JsonValidationSwaggerRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        //REST IMPLEMENTATION
        restConfiguration()
               .inlineRoutes("true")
                .component("servlet")
//                .bindingMode(RestBindingMode.json)
//                .enableCORS(true)
                .host("localhost")
                .scheme("https")
                .contextPath("/api")
                .dataFormatProperty("prettyPrint", "true")
//                .apiContextPath("/api-docs")
                .apiHost("localhost")
                .apiProperty("base.path", "/v3")
                .apiProperty("base.path", "/v1")
//                .apiProperty("api.title", "Json Schema Vadilation")
                .apiProperty("api.version", "1.0.0");


        rest("/v3")
                .get("/hello")
                .produces("text/plain")
                .to("direct:somewhere");

        from("direct:somewhere")
                .process(exchange -> exchange.getIn().setBody("hello world!"))
                .process(exchange -> exchange.getIn().setHeader(HTTP_RESPONSE_CODE, HTTP_OK));

        //REST DSL IMPLEMENTATION
        rest("/v3")
                .post("/json-schema-validation")
                .consumes("application/json")
                .produces("text/plain")
                .param()
                .name("body")
                .type(RestParamType.body)
                .endParam()
                .to("direct:jsonValidation")
                //swagger annotations
                .description("JsonSchemaValidation")
                .responseMessage().code(200).message("Valid Json Data").endResponseMessage();
//                .responseMessage().code(500).message("Invalid Json Data").endResponseMessage();

        rest("/v1")
                .post("/xml-schema-validation")
                .consumes("application/xml")
                .produces("text/plain")
                .param()
                .name("body")
                .type(RestParamType.body)
                .endParam()
                .to("direct:xmlValidation")
                //swagger annotations
                .description("XmlSchemaValidation")
                .responseMessage().code(200).message("Valid xml Data").endResponseMessage();
    }

}
