package org.meveo.commons.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

/**
 * @author Tyshan　Shi(tyshan@manaty.net)
 * @date Aug 24, 2016 3:07:56 PM
 **/
public class XStreamCDATAConverter implements Converter {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	 
	private static final String CDATA_START = "<![CDATA[";
	private static final String CDATA_END = "]]>";

	@SuppressWarnings("rawtypes")
	@Override
	public boolean canConvert(Class clazz) {
		return true;
	}

	@Override
	public void marshal(Object object, HierarchicalStreamWriter writer, MarshallingContext context) {
		log.debug("start marshall...");
		if (object == null) {
			writer.setValue("");
		} else {
			writer.setValue(CDATA_START+object+CDATA_END);
		}

	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		log.debug("start to unmarshall...");
		return null;
	}

}
