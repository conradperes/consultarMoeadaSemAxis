package com.cds.bacenConsultaMoedaClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.XML;
import org.springframework.stereotype.Component;

import com.google.common.io.CharStreams;

@Component(value = "moedaBean")
public class MoedaBean {
	private static final String ENDPOINT_ADDRESS = "https://www3.bcb.gov.br/wssgs/services/FachadaWSSGS";
	private static final String ENCODING_CHARACTER = "ISO-8859-1";
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;

	public void print(Object o) {
		System.out.println(o);
	}

	public String consultaCotacao(Long codSerie) throws ClientProtocolException, IOException {
		String json = null;
		if (codSerie != null && codSerie != 0) {
			StringBuilder body = buildBody(codSerie);
			StringEntity stringEntity = new StringEntity(body.toString(), ENCODING_CHARACTER);
			stringEntity.setChunked(true);
			// print("Body of SOAP \t" + body.toString() + "\t\n");
			// Request parameters and other properties.
			HttpPost httpPost = new HttpPost(ENDPOINT_ADDRESS);
			httpPost.setEntity(stringEntity);
			httpPost.addHeader("Accept", "application/xml");
			httpPost.addHeader("SOAPAction", null);

			// Execute and get the response.
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity entity = response.getEntity();

			String strResponse = null;
			if (entity != null) {
				strResponse = IOUtils.toString(entity.getContent(), ENCODING_CHARACTER);// EntityUtils.toString(entity);
			}
			// String json=xmlToJson(strResponse);
			// MyTransformer transformer = new MyTransformer();
			// String json = transformer.parseXmlToJson(strResponse);
			// print("\t" + json + "\t");
			// print("String com namespace=" + strResponse);
			String responseSemNameSpaces = removeNameSpaces(strResponse);
			// print("Resposta sem Namespace=" + responseSemNameSpaces);
			MyTransformer transformer = new MyTransformer();
			json = transformer.parseXmlToJson(responseSemNameSpaces);
		} else {
			json = "";
		}
		// print("json=" + json);
		return json;
	}

	private String removeNameSpaces(String response) {
		String responseSemNamespace = response.split("xml version='1.0'")[1].substring(1)
				.split("</getUltimoValorXMLReturn>")[0];
		String xmlFinal = "<?xml version='1.0' e" + responseSemNamespace.substring(1, responseSemNamespace.length() - 3)
				.replaceAll("&lt;", "<").replaceAll("&gt;", ">").replaceAll("/resposta&", "/resposta>");
		// print("Resposta sem Namespace=" + xmlFinal);
		return xmlFinal;
	}

	private StringBuilder buildBody(Long codSerie) {
		StringBuilder body = new StringBuilder(
				"<soapenv:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance/\"" + "\n");
		body.append(
				"xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\""
						+ "\n");
		body.append("xmlns:pub=\"http://publico.ws.casosdeuso.sgs.pec.bcb.gov.br\">\n");
		body.append("<soapenv:Header/>\n");
		body.append(" <soapenv:Body>\n");
		body.append("  <pub:getUltimoValorXML soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
		body.append(" <in0 xsi:type=\"xsd:long\">" + codSerie + "</in0>\n");
		body.append("</pub:getUltimoValorXML>\n");
		body.append("</soapenv:Body>\n");
		body.append("</soapenv:Envelope>");
		return body;
	}

	private String getMoeda(String codSerie) {
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
			responseAsString = CharStreams.toString(new InputStreamReader(inputStream, ENCODING_CHARACTER));
			System.out.println(response.getStatusLine().getStatusCode());
			System.out.println("Executou o Moeda bean" + "\t" + responseAsString + "\t");
		} catch (IOException e) {
			e.printStackTrace();
		}

		return responseAsString;
	}

	private String xmlToJson(String xml) {
		String jsonPrettyPrintString = null;
		try {
			org.json.JSONObject xmlJSONObj = XML.toJSONObject(xml);
			jsonPrettyPrintString = xmlJSONObj.toString(PRETTY_PRINT_INDENT_FACTOR);
		} catch (JSONException je) {
			print(je.toString());
		}
		return jsonPrettyPrintString;
	}
}
