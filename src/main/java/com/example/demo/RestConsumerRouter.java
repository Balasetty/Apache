package com.example.demo;

import org.apache.camel.ValidationException;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;
import org.apache.camel.component.jsonvalidator.JsonValidationException;

import static java.net.HttpURLConnection.HTTP_OK;
import static org.apache.camel.Exchange.HTTP_RESPONSE_CODE;

@Component
public class RestConsumerRouter extends RouteBuilder {
    @Override
    public void configure() throws Exception {

        from("direct:jsonValidation")
                .marshal().json()
                .log("Received Message:${body}")
                .doTry()
                .to("json-validator:my-schema.json")
                .to("direct:jsonprocessstart")

//                .to("mock:end")
                .doCatch(JsonValidationException.class)
//                .log("Vadilation check");
                .to("direct:error");

        from("direct:error")
                .log("validation error:${exception.message}")
                .process(exchange -> exchange.getIn().setHeader(HTTP_RESPONSE_CODE, HTTP_OK))
                .process(exchange -> {
                    exchange.getIn().setBody("Invalid json data");
                })
                .end();

        from("direct:jsonprocessstart")
                .log("Valid json data")
                .process(exchange -> exchange.getIn().setHeader(HTTP_RESPONSE_CODE, HTTP_OK))
                .process(exchange -> {
                    exchange.getIn().setBody("Valid json data");
                })
                .end();


        from("direct:xmlValidation")
//                .marshal().json()
                .log("Received Message:${body}")
                .doTry()
                .to("validator:xml.xsd")
                .to("direct:xmlprocessstart")
        .doCatch(ValidationException.class)
//                .log("Vadilation check");
                .to("direct:errorxml");
        from("direct:errorxml")
                .log("validation error:${exception.message}")
                .process(exchange -> exchange.getIn().setHeader(HTTP_RESPONSE_CODE, HTTP_OK))
                .process(exchange -> {
                    exchange.getIn().setBody("Invalid xml data");
                })
                .end();
        from("direct:xmlprocessstart")
                .log("Valid xml data")
                .process(exchange -> exchange.getIn().setHeader(HTTP_RESPONSE_CODE, HTTP_OK))
                .process(exchange -> {
                    exchange.getIn().setBody("Valid xml data");
                })
                .end();
    }
}
