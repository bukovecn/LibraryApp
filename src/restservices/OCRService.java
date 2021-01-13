package restservices;

import java.awt.image.DataBufferDouble;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import data.OcrData;

@Path("/ocr")
public class OCRService {
	
	public static final String mrtd_api_url = "https://api.microblink.com/v1/recognizers/mrtd";
	public static final String create_user_api_url = "http://localhost:8080/Library/libraryapi/user/";

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createUser(OcrData ocr) {
		
		try {

			Client client = Client.create();	
	        WebResource webResource = client.resource(mrtd_api_url);

	        JSONObject jsonBody = new JSONObject();
	        jsonBody.put("returnFullDocumentImage", false);
	        jsonBody.put("returnFaceImage", false);
	        jsonBody.put("returnSignatureImage", false);
	        jsonBody.put("allowBlurFilter", false);
	        jsonBody.put("allowUnparsedMrzResults", false);
	        jsonBody.put("allowUnverifiedMrzResults", false);
	        jsonBody.put("anonymizationMode", "FULL_RESULT");
	        jsonBody.put("anonymizeImage", true);
	        jsonBody.put("ageLimit", 0);
	        jsonBody.put("imageSource", ocr.getImageSource());
	        		
	        ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
	        							.header("Authorization", "Bearer ZDEyMzQzMDUxZDkxNDQ1M2FlZGIxYmQ4ODk4MzlkZjQ6Y2IzM2I2NWUtMGViMy00ZmIxLTk2ZGItZmFmM2I1NGIzODU1")
	        							.type(MediaType.APPLICATION_JSON_TYPE)
	        							.post(ClientResponse.class, jsonBody.toString());
       
	        
	        if (response.getStatus() != 200) {
	            throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
	        }

	        
	        String output = response.getEntity(String.class);
	        System.out.println("Output from Microblink API:");
	        System.out.println(output);

	        JSONObject resp = new JSONObject(output);
	        JSONObject result = resp.getJSONObject("result");
	        JSONObject mrzData = result.getJSONObject("mrzData");
	        String rawMrzString = mrzData.getString("rawMrzString");
	        String[] data = rawMrzString.split("\n");	    
	        
	        String[] fistLastName = data[2].split("<<", 3);	        
	        String lastname = fistLastName[0];
	        String firstname = fistLastName[1];
	        String dateOfBirth = data[1].substring(0, 6);
	        char dateCheckDigit = data[1].charAt(6);
	        
	        //check digits validation
	        boolean validDateOfBirth = validateData(dateOfBirth, dateCheckDigit);
	        
	        String cardNumber = data[0].substring(5, 14);
	        char cardNumChechDigit = data[0].charAt(14);
	        
	        boolean validCardNumber = validateData(cardNumber, cardNumChechDigit);
			
	        
	        //call POST for adding user
	        callCreateUserService(client, mrzData, lastname, firstname, validCardNumber, validDateOfBirth);

	        
	        return Response.status(Response.Status.OK).entity(output.toString()).build();
	        
	      } catch (Exception e) {
	    	  e.printStackTrace();
	    	  return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
	      }

	}

	private void callCreateUserService(Client client, JSONObject mrzData, String lastname, String firstname, boolean validCardNumber, boolean validDateOfBirth) {
		WebResource webResource = client.resource(create_user_api_url);
		
		JSONObject jsonUser = new JSONObject();
		jsonUser.put("firstname", firstname);
		jsonUser.put("lastname", lastname);
		
		String dateString = "";
		JSONObject dateParsed = mrzData.getJSONObject("dateOfBirth");
		int day = dateParsed.getInt("day");
		int month = dateParsed.getInt("month");	  
		int year = dateParsed.getInt("year");
		dateString += year;	             
		if(month < 10) {
			dateString += "-0" + month;
		}else {
			dateString += "-" + month;
		}		
		dateString += "-" +day;		
		jsonUser.put("date_of_birth", dateString);
		
		if(validCardNumber && validDateOfBirth)
			jsonUser.put("verified", true);
		else
			jsonUser.put("verified", false);
		
		ClientResponse response = webResource.accept(MediaType.APPLICATION_JSON_TYPE)
				.type(MediaType.APPLICATION_JSON_TYPE)
				.post(ClientResponse.class, jsonUser.toString());

		if (response.getStatus() != 200) {
		    throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
		}
	}

	private boolean validateData(String stringToBeChecked, char checkDigit) {
		boolean valid = false;
				
		int length = stringToBeChecked.length();
		
		int[] weightDigits = {7, 3, 1};
		int[] weighting = new int[length];
		int[] arrayOfDigits = new int[length];
				
		
		String[] arrayDigitsString = stringToBeChecked.codePoints().mapToObj(cp -> new String(Character.toChars(cp))).toArray(size -> new String[size]);
		
		for(int i = 0; i<arrayDigitsString.length; i++) {
			arrayOfDigits[i] = Integer.parseInt(arrayDigitsString[i]);
		}
		
		int i = 0;
		while(i<length) {
			for(int j = 0; j<weightDigits.length; j++) {				
				weighting[i] = weightDigits[j];				
				i++;				
			}
		}
		
		//Step 1 (multiplication)
		List<Integer> sums = new ArrayList<Integer>();
		for(int a=0; a<length; a++) {
			int multiplication = arrayOfDigits[a] * weighting[a];
			sums.add(multiplication);
		}
		
		//Step 2 (sum of products)
		int sum = 0;
		for (Integer s : sums) {
			sum += s;
		}
		
		//Step 3 (division by modulus) 
		int modulo = sum % 10;
		
		int checkDig = Integer.parseInt(new String(Character.toChars(checkDigit)));
		
		if(modulo == checkDig) {
			valid = true;
		}
		
		return valid;
	}
}
