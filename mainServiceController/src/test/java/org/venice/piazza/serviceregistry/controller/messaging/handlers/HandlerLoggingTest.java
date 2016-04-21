package org.venice.piazza.serviceregistry.controller.messaging.handlers;

import static org.junit.Assert.assertTrue;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.venice.piazza.servicecontroller.data.mongodb.accessors.MongoAccessor;
import org.venice.piazza.servicecontroller.messaging.handlers.DescribeServiceHandler;
import org.venice.piazza.servicecontroller.messaging.handlers.ExecuteServiceHandler;
import org.venice.piazza.servicecontroller.messaging.handlers.ListServiceHandler;
import org.venice.piazza.servicecontroller.messaging.handlers.RegisterServiceHandler;
import org.venice.piazza.servicecontroller.messaging.handlers.SearchServiceHandler;
import org.venice.piazza.servicecontroller.messaging.handlers.UpdateServiceHandler;
import org.venice.piazza.servicecontroller.util.CoreServiceProperties;

import model.data.DataType;
import model.data.type.BodyDataType;
import model.data.type.TextDataType;
import model.data.type.URLParameterDataType;
import model.job.metadata.ResourceMetadata;
import model.job.type.DescribeServiceMetadataJob;
import model.job.type.ListServicesJob;
import model.job.type.RegisterServiceJob;
import model.job.type.SearchServiceJob;
import model.job.type.UpdateServiceJob;
import model.service.SearchCriteria;
import model.service.metadata.ExecuteServiceData;
import model.service.metadata.Format;
import model.service.metadata.MetadataType;
import model.service.metadata.ParamDataItem;
import model.service.metadata.Service;
import util.PiazzaLogger;
import util.UUIDFactory;

@RunWith(PowerMockRunner.class)
public class HandlerLoggingTest {

	static String logString = "";
	ResourceMetadata rm = null;
	Service service = null;
	RestTemplate template = null;
	MongoAccessor mockMongo = null;
	PiazzaLogger logger = null;
	CoreServiceProperties props = null;
	@Before
    public void setup() {
		template = mock(RestTemplate.class);
		try {
			whenNew(RestTemplate.class).withNoArguments().thenReturn(template);
		} catch (Exception e) {
		 	// TODO Auto-generated catch block
				e.printStackTrace();
		}
		rm = new ResourceMetadata();
		rm.name = "toUpper Params";
		rm.description = "Service to convert string to uppercase";
		rm.method = "POST";
		service = new Service();
		service.setResourceMetadata(rm);
		service.setServiceId("8");
		service.setUrl("http://localhost:8085/string/toUpper");
		
		mockMongo = mock(MongoAccessor.class);
		when(mockMongo.save(service)).thenReturn("8");
		when(mockMongo.getServiceById("8")).thenReturn(service);
		logger = mock(PiazzaLogger.class);
		props = mock(CoreServiceProperties.class);
    	
    }
	@Test
	public void TestExecuteServiceHandlerMimeTypeErrorLogging() {
		String upperServiceDef = "{  \"name\":\"toUpper Params\"," +
		        "\"description\":\"Service to convert string to uppercase\"," + 
		        "\"url\":\"http://localhost:8082/string/toUpper\"," + 
		         "\"method\":\"POST\"," + "\"params\": [\"aString\"]" +
		         /*"\"params\": [\"aString\"]," + 
		         "\"mimeType\":\"application/json\"" +*/
		       "}";
		
		
		
		ExecuteServiceData edata = new ExecuteServiceData();
		
		edata.setServiceId("8");
		
		HashMap<String,DataType> dataInputs = new HashMap<String,DataType>();
		String istring = "The rain in Spain falls mainly in the plain";
		BodyDataType body = new BodyDataType();
		body.content = istring;
		dataInputs.put("Body", body);
		edata.setDataInputs(dataInputs);
		
		
		
		
		URI uri = URI.create("http://localhost:8085//string/toUpper");
		when(template.postForEntity(Mockito.eq(uri),Mockito.any(Object.class),Mockito.eq(String.class))).thenReturn(new ResponseEntity<String>("testExecuteService",HttpStatus.FOUND));
		String mimeError = "Body mime type not specified";
		doAnswer(new Answer() {
			
			    public Object answer(InvocationOnMock invocation) {
			
			        Object[] args = invocation.getArguments();
			        logString = args[0].toString();
			
			       
			       
			
			        return null;
			
			    }}).when(logger).log(Mockito.anyString(),Mockito.anyString());
		
		ExecuteServiceHandler handler = new ExecuteServiceHandler(mockMongo,props,logger);
		ResponseEntity<String> retVal = handler.handle(edata);
		assertTrue(logString.contains("Body mime type not specified"));
	    
	}
	
	@Test
	public void TestSearchServiceHandlerCorrectLogging() {
		
		SearchServiceJob sjob = new SearchServiceJob();
		SearchCriteria criteria = new SearchCriteria();
		criteria.setField("description");
		criteria.setPattern("*bird*");
		sjob.data = criteria;
		logString = "";
		
		SearchServiceHandler searchHandler = new SearchServiceHandler(mockMongo,props,logger);
		doAnswer(new Answer() {
					
				    public Object answer(InvocationOnMock invocation) {
				
				        Object[] args = invocation.getArguments();
				        logString = args[0].toString();
				        return null;
				
		}}).when(logger).log(Mockito.anyString(),Mockito.anyString());
		ArrayList<Service> services = new ArrayList<Service>();
		services.add(service);
		when(mockMongo.search(criteria)).thenReturn(services);
		searchHandler.handle(sjob);
		assertTrue(logString.contains("About to search using criteria"));
				
	}
	@Test
	public void TestSearchServiceHandlerNoResultsLogging() {
		
		SearchServiceJob sjob = new SearchServiceJob();
		SearchCriteria criteria = new SearchCriteria();
		criteria.setField("description");
		criteria.setPattern("*bird*");
		sjob.data = criteria;
		logString = "";
		
		SearchServiceHandler searchHandler = new SearchServiceHandler(mockMongo,props,logger);
		doAnswer(new Answer() {
					
				    public Object answer(InvocationOnMock invocation) {
				
				        Object[] args = invocation.getArguments();
				        logString = args[0].toString();
				        return null;
				
		}}).when(logger).log(Mockito.anyString(),Mockito.anyString());
		when(mockMongo.search(criteria)).thenReturn(new ArrayList<Service>());
		searchHandler.handle(sjob);
		assertTrue(logString.contains("No results"));
				
	}
	
	@Test
	public void TestDescribeServiceHandlerSuccessLogging() {
		DescribeServiceMetadataJob dsmJob = new DescribeServiceMetadataJob();
		dsmJob.serviceID = "8";
		DescribeServiceHandler handler = new DescribeServiceHandler(mockMongo,props,logger);
		doAnswer(new Answer() {
			
		    public Object answer(InvocationOnMock invocation) {
		
		        Object[] args = invocation.getArguments();
		        logString = args[0].toString();
		        return null;
		
		 }}).when(logger).log(Mockito.anyString(),Mockito.anyString());
		 when(mockMongo.getServiceById("8")).thenReturn(service);
		 handler.handle(dsmJob);
		 assertTrue(logString.contains("Describing a service"));
		 
		
	}
	
	@Test
	public void TestListServiceHandlerFailLogging() {
		ListServicesJob lsj = new ListServicesJob();
		ListServiceHandler handler = new ListServiceHandler(mockMongo,props,logger);
		ArrayList<Service> services = new ArrayList<Service>();
		services.add(service);
		doAnswer(new Answer() {
			
		    public Object answer(InvocationOnMock invocation) {
		
		        Object[] args = invocation.getArguments();
		        logString = args[0].toString();
		        return null;
		
		 }}).when(logger).log(Mockito.anyString(),Mockito.anyString());
		NullPointerException ex = new NullPointerException("Test Error");
		when(mockMongo.list()).thenThrow(ex);
		handler.handle(lsj);
		assertTrue(logString.contains(ex.getMessage()));
		
		
	}
	@Test
	public void TestListServiceHandlerLogging() {
		ListServicesJob lsj = new ListServicesJob();
		ListServiceHandler handler = new ListServiceHandler(mockMongo,props,logger);
		ArrayList<Service> services = new ArrayList<Service>();
		services.add(service);
		doAnswer(new Answer() {
			
		    public Object answer(InvocationOnMock invocation) {
		
		        Object[] args = invocation.getArguments();
		        logString = args[0].toString();
		        return null;
		
		 }}).when(logger).log(Mockito.anyString(),Mockito.anyString());
		when(mockMongo.list()).thenReturn(services);
		handler.handle(lsj);
		assertTrue(logString.contains("listing service"));
		
		
	}
	@Test
	public void TestDescribeServiceHandlerFailLogging() {
		DescribeServiceMetadataJob dsmJob = new DescribeServiceMetadataJob();
		dsmJob.serviceID = "8";
		DescribeServiceHandler handler = new DescribeServiceHandler(mockMongo,props,logger);
		doAnswer(new Answer() {
			
		    public Object answer(InvocationOnMock invocation) {
		
		        Object[] args = invocation.getArguments();
		        logString = args[0].toString();
		        return null;
		
		 }}).when(logger).log(Mockito.anyString(),Mockito.anyString());
		NullPointerException ex = new NullPointerException();
		 when(mockMongo.getServiceById("8")).thenThrow(ex);
		 handler.handle(dsmJob);
		 assertTrue(logString.contains("Could not retrieve resourceId"));
		 
		
	}

	@Test
	public void TestRegisterServiceHandlerLogging() {
		UUIDFactory uuidFactory = mock(UUIDFactory.class);
	    when(uuidFactory.getUUID()).thenReturn("NoDoz");
		template = mock(RestTemplate.class);
		try {
			whenNew(RestTemplate.class).withNoArguments().thenReturn(template);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rm = new ResourceMetadata();
		rm.name = "toUpper Params";
		rm.description = "Service to convert string to uppercase";
		rm.method = "POST";
		service.setResourceMetadata(rm);
		service.setServiceId("8");
		service.setUrl("http://localhost:8082/string/toUpper");
		
		RegisterServiceJob rjob = new RegisterServiceJob();
		rjob.data = service;
		RegisterServiceHandler handler = new RegisterServiceHandler(mockMongo,props,logger,uuidFactory);
		
		doAnswer(new Answer() {
			
		    public Object answer(InvocationOnMock invocation) {
		
		        Object[] args = invocation.getArguments();
		        logString = args[0].toString();
		        return null;
		
		    }}).when(logger).log(Mockito.anyString(),Mockito.anyString());
	
	     handler.handle(rjob);
	     assertTrue(logString.contains("serviceMetadata received"));
		
	}
	
	@Test
	public void TestUpdateServiceHandlerSuccessLogging() {
		UUIDFactory uuidFactory = mock(UUIDFactory.class);
	    when(uuidFactory.getUUID()).thenReturn("NoDoz");
		template = mock(RestTemplate.class);
		try {
			whenNew(RestTemplate.class).withNoArguments().thenReturn(template);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rm = new ResourceMetadata();
		rm.name = "toUpper Params";
		rm.description = "Service to convert string to uppercase";
		service.setResourceMetadata(rm);
		service.setServiceId("8");
		service.setUrl("http://localhost:8082/string/toUpper");
		ParamDataItem pitem = new ParamDataItem();
		
		UpdateServiceJob rjob = new UpdateServiceJob();
		rjob.data = service;
		UpdateServiceHandler handler = new UpdateServiceHandler(mockMongo,props,logger,uuidFactory);
		
		doAnswer(new Answer() {
			
		    public Object answer(InvocationOnMock invocation) {
		
		        Object[] args = invocation.getArguments();
		        logString = args[0].toString();
		        return null;
		
		    }}).when(logger).log(Mockito.anyString(),Mockito.anyString());
	
		when(mockMongo.update(service)).thenReturn("8");
	     handler.handle(rjob);
	     assertTrue(logString.contains("was updated"));
		
	}
	@Test
	public void TestUpdateServiceHandlerFailLogging() {
		UUIDFactory uuidFactory = mock(UUIDFactory.class);
	    when(uuidFactory.getUUID()).thenReturn("NoDoz");
		template = mock(RestTemplate.class);
		try {
			whenNew(RestTemplate.class).withNoArguments().thenReturn(template);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rm = new ResourceMetadata();
		rm.name = "toUpper Params";
		rm.description = "Service to convert string to uppercase";
		
		rm.method = "POST";
		service.setResourceMetadata(rm);
		service.setServiceId("8");
		service.setUrl("http://localhost:8082/string/toUpper");
		
		UpdateServiceJob rjob = new UpdateServiceJob();
		rjob.data = service;
		UpdateServiceHandler handler = new UpdateServiceHandler(mockMongo,props,logger,uuidFactory);
		
		doAnswer(new Answer() {
			
		    public Object answer(InvocationOnMock invocation) {
		
		        Object[] args = invocation.getArguments();
		        logString = args[0].toString();
		        return null;
		
		    }}).when(logger).log(Mockito.anyString(),Mockito.anyString());
	
		when(mockMongo.update(service)).thenReturn("");
	     handler.handle(rjob);
	     assertTrue(logString.contains("something went wrong"));
		
	}


}