/*******************************************************************************
 * Copyright 2016, RadiantBlue Technologies, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package org.venice.piazza.servicecontroller.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.venice.piazza.servicecontroller.data.model.Message;

import util.PiazzaLogger;

@RestController
/**
 * Service that performs String operations.  This class is to support testing of the
 * service controller.
 * 
 * @author mlynum
 */

@RequestMapping("/jumpstart")
public class GettingStartedController {

	private final static int MESSSAGE_COUNT = 15;
	
	private final static String ORACLE_WELCOME="Here, take a cookie. I promise, by the time you're done eating it, you'll feel right as rain.";
	private final static String JAWS_WELCOME="You're gonna need a bigger boat.";
	private final static String ANCHORMAN_WELCOME="I'm Ron Burgandyyyy?";
	private final static String BATTLESTAR_WELCOME="So say we all.";
	private final static String PREDATOR_WELCOME="If it bleeds, we can kill it.";
	private final static String FEWGOODMEN_WELCOME="You want me on that wall. You NEED me on that wall";
	private final static String GOODFATHER_WLECOME="Leave the gun. Take the cannoli.";
	private final static String HUNTREDOCTOBER_WELCOME="Re-verify our range to target... one ping only. ";
	private final static String HEAT_WELCOME = "Clean up, go home.";
	private final static String DUNE_WELCOME="And how can this be? For he is the Kwisatz Haderach!";
	private final static String USUAL_SUSPECTS_WELCOME="The greatest trick the Devil " +
														"ever pulled was convincing the world he didn't exist. " +
														"And like that, poof. He's gone.";
	private final static String PASSENGER57_WELCOME="Ever played roulette?......Well, let me give you a word of advice..... Always bet on black!";
	private final static String DEVILWEARSPRADA_WELCOME= "Why is no one ready...?";
	private final static String FUNNYFARM_WELCOME="Cue the deer.";
	private final static String PRESTIGE_WELCOME="You always were the better magician, we both know that. "+
												"But whatever your secret was, you will have to agree, mine is better......";
												 		
	
	String[] welcomeMessages = {ORACLE_WELCOME, JAWS_WELCOME, ANCHORMAN_WELCOME, BATTLESTAR_WELCOME, 
								PREDATOR_WELCOME, FEWGOODMEN_WELCOME, GOODFATHER_WLECOME, HUNTREDOCTOBER_WELCOME,
								HEAT_WELCOME, DUNE_WELCOME, USUAL_SUSPECTS_WELCOME, PASSENGER57_WELCOME,
								DEVILWEARSPRADA_WELCOME, FUNNYFARM_WELCOME, PRESTIGE_WELCOME};   
	
	@Autowired
	private PiazzaLogger logger;
	/**
	 * Rest call to convert a string to upper case
	 * Access 
	 * http://localhost:8080/jumpstart/string/toUpper?aString=<a string> 
	 * 
	 * @param aString
	 * @return JSON {result:<the converted string>}
	 */	 
	@RequestMapping(value = "/string/toUpper", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public String convertStringtoUpper(@ModelAttribute("aString") String aString ) {
		String result = "a String was not provided.";
				
		if (aString != null)
			result = aString.toUpperCase();
	
        logger.log("The result is " + result, PiazzaLogger.INFO);
 
        return "{\"result\":\"" + result + "\"}";
    }
	

	
	/**
	 * Rest call to convert a string in a message to upper or lower case
	 * Access http://localhost:8080/jumpstart/string/convert
	 * Accepts JSON: 
	 * { 
       "theString":"<a string>",
       "conversionType":"LOWER" // UPPER or LOWER
     * }
	 * @param msg
	 * @return JSON {result:<the converted string>}
	 */
	@RequestMapping(value = "/string/convert", method = RequestMethod.POST, headers="Accept=application/json", produces=MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody String convert(@RequestBody Message msg) {
		
		String result = "Could not Convert, please check message";
		String conversionType = msg.getConversionType();
		String theString = msg.gettheString();
		if ((conversionType != null) && (theString != null)) {
			if (conversionType.equals(Message.UPPER))  {
		        logger.log("Make the String uppercase" + theString, PiazzaLogger.INFO);
		        logger.log("The message" + msg, PiazzaLogger.INFO);
		        result=convertStringtoUpper(theString);
			} 
			else if (conversionType.equals(Message.LOWER))  {
		        logger.log("Make the String lower case" + theString, PiazzaLogger.INFO);
				result=convertStringtoLower(theString);
		       
			}
		}
		
		return result;
		
	}
	/**
	 * Rest call to convert a string to upper case
	 * Access 
	 * http://localhost:8080/jumpstart/string/toLower?aString=<a string> 
	 * 
	 * @param aString
	 * @return JSON {result:<the converted string>}
	 */	 
	@RequestMapping(value = "/string/toLower", method = RequestMethod.POST, produces=MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
    public String convertStringtoLower(@ModelAttribute("aString") String aString) {        
        String result = "aString was not provided";
        
        if(aString != null)
        	result  = aString.toLowerCase();
        logger.log("The result is " + result, PiazzaLogger.INFO);
        
        return "{\"result\":\"" + result + "\"}";

    }
	
	/**
	 * Provide a movie welcome to pz-service controller
	 * Access 
	 * 
	 * @param none
	 * @return JSON {result:<A greeting>}
	 */	 
	@RequestMapping(value = "/moviequotewelcome/{name}", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public String movieWelcome(@PathVariable("name") String name ) {

		String message = "";
		 int msgNum = getRandomNumber();
		 message= welcomeMessages[msgNum];
        logger.log("Generate a hearty movie welcome", PiazzaLogger.INFO);
        if (name != null) {
        	logger.log("User is " + name, PiazzaLogger.INFO);
        	 message = message + "\n\nHELLO " + name + "!!!!\n";
        }
       
        message = message + "Welcome to the piazza pz-servicecontroller!\n";
        message = message + "Details on using pz-servicecontrollers are \n";
        message = message + "here https://github.com/venicegeo/venice/wiki/Pz-ServiceController";
       
        logger.log("Welcome generated" + message, PiazzaLogger.INFO);
        return "{\"message\":\"" + message+ "\"}";
    }
	
	/**
	 * Provide a movie welcome to pz-service controller
	 * Access 
	 * 
	 * @param none
	 * @return JSON {result:<A greeting>}
	 */	 
	@RequestMapping(value = "/moviequotewelcome", method = RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
    public String movieWelcomeParms(@RequestParam(value="name", defaultValue="World") String name ) {

		String message = "";
		 int msgNum = getRandomNumber();
		 message= welcomeMessages[msgNum];
        logger.log("Generate a hearty movie welcome", PiazzaLogger.INFO);
        if (name != null) {
        	logger.log("User is " + name, PiazzaLogger.INFO);
        	 message = message + "\n\nHELLO " + name + "!!!!\n";
        }
       
        message = message + "Welcome to the piazza pz-servicecontroller!\n";
        message = message + "Details on using pz-servicecontrollers are \n";
       
        logger.log("Welcome generated" + message, PiazzaLogger.INFO);
        return "{\"message\":\"" + message+ "\"}";
    }
	
	
	
	private int getRandomNumber() {
		int randomInt = 0;

		Random rand = new Random();
		randomInt = rand.nextInt(MESSSAGE_COUNT);

		return randomInt;
	}
	
	
	

	
}
