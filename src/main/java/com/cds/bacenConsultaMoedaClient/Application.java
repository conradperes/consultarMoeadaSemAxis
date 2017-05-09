/*
 * Copyright 2016 Red Hat, Inc.
 * <p>
 * Red Hat licenses this file to you under the Apache License, version
 * 2.0 (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.  See the License for the specific language governing
 * permissions and limitations under the License.
 *
 */
package com.cds.bacenConsultaMoedaClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.bind.annotation.XmlAttribute;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.stereotype.Component;

import com.google.common.io.CharStreams;

@SpringBootApplication
// load regular Spring XML file from the classpath that contains the Camel XML
// DSL
@ImportResource({ "classpath:spring/camel-context.xml" })
public class Application {

	/**
	 * A main method to start this application.
	 */
	public static void main(String[] args) {
		// SpringApplication.run(Application.class, args);
		try {
			print(callWebService(null, null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void print(Object o) {
		System.out.println(o);
	}

	public static String getMoeda(String codSerie) {
		System.out.println("Código Série digitado no header =" + codSerie);
		String responseAsString = null;
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet("https://www3.bcb.gov.br/wssgs/services/FachadaWSSGS?in0=1");
		HttpResponse response = null;
		try {
			Header[] allHeaders = request.getAllHeaders();
			for (Header h : allHeaders) {
				System.out.println(h.getName());
				System.out.println(h.getValue());

			}
			HttpParams params = request.getParams();
			print(params.getParameter("in0"));
			print(request.getMethod());
			response = client.execute(request);

			InputStream inputStream = response.getEntity().getContent();
			responseAsString = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
			System.out.println(response.getStatusLine().getStatusCode());
			System.out.println("Executou o Cerberus bean" + responseAsString);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseAsString;
	}

	public static String callWebService(String soapAction, String soapEnvBody) throws ClientProtocolException, IOException {
		// Create a StringEntity for the SOAP XML.
		//String body = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><SOAP-ENV:Envelope xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:ns1=\"http://example.com/v1.0/Records\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:SOAP-ENC=\"http://schemas.xmlsoap.org/soap/encoding/\" SOAP-ENV:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"><SOAP-ENV:Body>"
			//	+ soapEnvBody + "</SOAP-ENV:Body></SOAP-ENV:Envelope>";
		StringBuilder body = new StringBuilder("<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance");
		body.append(
				"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/");
		body.append("xmlns:pub=\"http://publico.ws.casosdeuso.sgs.pec.bcb.gov.br\">");
		body.append("<soapenv:Header/>");
		body.append(" <soapenv:Body>");
		body.append("  <pub:getUltimoValorXML soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">");
		body.append(" <in0 xsi:type=\"xsd:long\">1</in0>");
		body.append("</pub:getUltimoValorXML>");
		body.append("</soapenv:Body>");
		body.append("</soapenv:Envelope>");
		StringEntity stringEntity = new StringEntity(body.toString(), "UTF-8");
		stringEntity.setChunked(true);

		// Request parameters and other properties.
		HttpPost httpPost = new HttpPost("https://www3.bcb.gov.br/wssgs/services/FachadaWSSGS");
		httpPost.setEntity(stringEntity);
		httpPost.addHeader("Accept", "text/xml");
		httpPost.addHeader("SOAPAction", soapAction);

		// Execute and get the response.
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();

		String strResponse = null;
		if (entity != null) {
			strResponse = EntityUtils.toString(entity);
		}
		return strResponse;
	}
}