package com.cds.pcrj.bacenConsultaMoedaSemAxisIntegration;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.XML;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Component;

import com.google.common.io.CharStreams;

@Component(value = "moedaBean")
public class MoedaBean {
	public static int PRETTY_PRINT_INDENT_FACTOR = 4;

	public void print(Object o) {
		System.out.println(o);
	}

	public String consultaCotacao(Long codSerie) throws ClientProtocolException, IOException {
		StringBuilder body = buildBody(codSerie);
		StringEntity stringEntity = new StringEntity(body.toString(), "UTF-8");
		stringEntity.setChunked(true);
		print("Body of SOAP \t" + body.toString() + "\t\n");
		// Request parameters and other properties.
		HttpPost httpPost = new HttpPost("https://www3.bcb.gov.br/wssgs/services/FachadaWSSGS");
		httpPost.setEntity(stringEntity);
		httpPost.addHeader("Accept", "application/xml");
		httpPost.addHeader("SOAPAction", null);

		// Execute and get the response.
		HttpClient httpClient = new DefaultHttpClient();
		HttpResponse response = httpClient.execute(httpPost);
		HttpEntity entity = response.getEntity();

		String strResponse = null;
		if (entity != null) {
			strResponse = EntityUtils.toString(entity);
		}
		// String json=xmlToJson(strResponse);
		MyTransformer transformer = new MyTransformer();
		String json = transformer.parseXmlToJson(strResponse);
		print("\t" + json + "\t");
		return json;
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
		body.append("  <pub:getUltimoValorVO soapenv:encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\">\n");
		body.append(" <in0 xsi:type=\"xsd:long\">" + codSerie + "</in0>\n");
		body.append("</pub:getUltimoValorVO>\n");
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
			responseAsString = CharStreams.toString(new InputStreamReader(inputStream, "UTF-8"));
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
