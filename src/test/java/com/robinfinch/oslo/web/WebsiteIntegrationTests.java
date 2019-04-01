package com.robinfinch.oslo.web;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.robinfinch.oslo.test.CaptureStateTransformer;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.web.context.WebApplicationContext;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.junit.Assert.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@ActiveProfiles("integrationtest")
public class WebsiteIntegrationTests {

    @Autowired
    private WebApplicationContext context;

    private WebClient webClient;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(wireMockConfig().port(8090)
            .extensions(CaptureStateTransformer.class));

    @Before
    public void setup() {

        // set up a Mock OAuth server
        stubFor(get(urlPathMatching("/oauth/authorize.*"))
                .willReturn(aResponse()
                        .withStatus(302)
                        .withHeader("Location", "http://localhost:8080/login/oauth2/code/my-oauth-client?code=my-acccess-code&state=${state}")
                        .withTransformers("CaptureStateTransformer")
                )
        );

        stubFor(post(urlPathMatching("/oauth/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"access_token\":\"my-access-token\"" +
                                ", \"token_type\":\"Bearer\"" +
                                ", \"expires_in\":\"3600\"" +
                                "}")
                )
        );

        stubFor(get(urlPathMatching("/userinfo"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"sub\":\"my-user-id\"" +
                                ",\"name\":\"Mark Hoogenboom\"" +
                                ", \"email\":\"mark.hoogenboom@example.com\"" +
                                "}")
                )
        );

        webClient = MockMvcWebClientBuilder
                .webAppContextSetup(context, springSecurity())
                .build();
    }

    @Test
    public void testGreeting() throws Exception {

        HtmlPage page = webClient.getPage("http://localhost:8080/");

        assertEquals("Welcome\r\n" +
                " Hello, Mark Hoogenboom!", page.asText());
    }
}
