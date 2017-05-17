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

import java.io.StringReader;
import java.io.StringWriter;

import org.springframework.stereotype.Component;

import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.copy.HierarchicalStreamCopier;
import com.thoughtworks.xstream.io.json.JettisonMappedXmlDriver;
import com.thoughtworks.xstream.io.xml.XppReader;

/**
 * A sample transform
 */
@Component(value = "myTransformer")
public class MyTransformer {
	static String convertedJsonFile = "/convertedFile.json";

	public String transform() {
		// lets return a random string
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < 3; i++) {
			int number = (int) (Math.round(Math.random() * 1000) % 10);
			char letter = (char) ('0' + number);
			buffer.append(letter);
		}
		return buffer.toString();
	}

	public String parseXmlToJson(String xml) {
		HierarchicalStreamReader sourceReader = new XppReader(new StringReader(xml));
		StringWriter buffer = new StringWriter();
		JettisonMappedXmlDriver jettisonDriver = new JettisonMappedXmlDriver();
		jettisonDriver.createWriter(buffer);
		HierarchicalStreamWriter destinationWriter = jettisonDriver.createWriter(buffer);
		//destinationWriter.startNode("soapenv:Envelope");
		HierarchicalStreamCopier copier = new HierarchicalStreamCopier();
		copier.copy(sourceReader, destinationWriter);
		return buffer.toString();
	}


//	public void xmlToJson(String xmlFile) throws Exception {
//		File xml = new File(xmlFile);
//		JAXBContext jc = JAXBContext.newInstance(DatabaseInventory.class);
//		Unmarshaller unmarshaller = jc.createUnmarshaller();
//		DatabaseInventory activity = (DatabaseInventory) unmarshaller.unmarshal(xml);
//
//		Marshaller marshaller = jc.createMarshaller();
//		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//		marshaller.setProperty(MarshallerProperties.MEDIA_TYPE, "application/json");
//		marshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, true);
//		FileOutputStream fos = new FileOutputStream(new File(convertedJsonFile));
//		marshaller.marshal(activity, fos);
//		fos.close();
//
//	}

}
