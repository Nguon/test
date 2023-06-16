package www.cellcard.com.kh.KHQRPrepaidInvoice.controller;

import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import www.cellcard.com.kh.KHQRPrepaidInvoice.service.KHQRService;
import www.cellcard.com.kh.KHQRPrepaidInvoice.utility.AESUtils;
import www.cellcard.com.kh.KHQRPrepaidInvoice.utility.LogFormatter;
import www.cellcard.com.kh.KHQRPrepaidInvoice.utility.LogFormatterKeys;

@RestController
@RequestMapping("/khqr/v1")
public class controller {

    @Autowired
    KHQRService service;

    @Value("${base-url}")
    String baseUrl;
    @Value("${base-cellcard-url}")
    String baseCellcardUrl;
 
    @Value("${crc-salt}")
    String crcKey;
    @Value("${redis-key}")
    String redisKey;

    @Autowired
    RedisTemplate<String,String> redis;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    SimpleDateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    @GetMapping(value="/getinvoice/id/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInvoicebyid(@PathVariable String id){

        LogFormatter logFormatter = new LogFormatter();
        HashMap<String,String> hmLog  = new HashMap<>();
        hmLog.put(LogFormatterKeys.service_name.getKey(), "KHQRInvoice");
        hmLog.put(LogFormatterKeys.method_name.getKey(), "callback");
        hmLog.put(LogFormatterKeys.channel.getKey(), "");
        hmLog.put(LogFormatterKeys.request_id.getKey(), id);
        hmLog.put(LogFormatterKeys.request_plan.getKey(), "");
        hmLog.put(LogFormatterKeys.account_id.getKey(), "");
        hmLog.put(LogFormatterKeys.error_code.getKey(), "");
        hmLog.put(LogFormatterKeys.error_message.getKey(), "");
        hmLog.put(LogFormatterKeys.client_ip.getKey(), "");
        hmLog.put(LogFormatterKeys.uuid.getKey(), "");
        hmLog.put(LogFormatterKeys.action.getKey(), "request");
        hmLog.put(LogFormatterKeys.step.getKey(), "1");
        hmLog.put(LogFormatterKeys.nei.getKey(), "wing");
        hmLog.put(LogFormatterKeys.api.getKey(),  "getinvoice");
        hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
        logger.info(logFormatter.getLogMessage(hmLog));

        String resultCode="";
        String resultMsg="";
        boolean result = false;
        String res = service.CallbackGet(baseUrl+"/invoicing/api/invoice/find-by-id/"+id, "", service.getToken());
        JSONObject response  = new JSONObject(res);

        if(response.has("result_code")){
            resultCode = response.optString("result_code","field result_code is empty");
            if(resultCode.equalsIgnoreCase("A403")|| resultCode.equalsIgnoreCase("A402")){
                redis.delete(redisKey);
                res = service.CallbackGet(baseUrl+"/invoicing/api/invoice/find-by-id/"+id, "", service.getToken());
            }
        }
        if(response.has("result_message")){
            resultMsg = response.optString("result_message" , "field result_message is empty");
            
        }
        if(response.has("result")){
            result = response.getBoolean("result");
            
        }
        if(result){
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "SUCCESS");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }else{
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "FAILURE");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }
        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @GetMapping(value="/getinvoice/order-refer-no/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getInvoicebyRefer(@PathVariable String id){
        LogFormatter logFormatter = new LogFormatter();
        HashMap<String,String> hmLog  = new HashMap<>();
        hmLog.put(LogFormatterKeys.service_name.getKey(), "KHQRInvoice");
        hmLog.put(LogFormatterKeys.method_name.getKey(), "callback");
        hmLog.put(LogFormatterKeys.channel.getKey(), "");
        hmLog.put(LogFormatterKeys.request_id.getKey(), id);
        hmLog.put(LogFormatterKeys.request_plan.getKey(), "");
        hmLog.put(LogFormatterKeys.account_id.getKey(), "");
        hmLog.put(LogFormatterKeys.error_code.getKey(), "");
        hmLog.put(LogFormatterKeys.error_message.getKey(), "");
        hmLog.put(LogFormatterKeys.client_ip.getKey(), "");
        hmLog.put(LogFormatterKeys.uuid.getKey(), "");
        hmLog.put(LogFormatterKeys.action.getKey(), "request");
        hmLog.put(LogFormatterKeys.step.getKey(), "1");
        hmLog.put(LogFormatterKeys.nei.getKey(), "wing");
        hmLog.put(LogFormatterKeys.api.getKey(),  "getinvoice");
        hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
        logger.info(logFormatter.getLogMessage(hmLog));
        String resultCode="";
        String resultMsg="";
        boolean result = false;
        String res = service.CallbackGet(baseUrl+"/invoicing/api/invoice/find-by-order-reference-no/"+id, "", service.getToken());
        JSONObject response  = new JSONObject(res);
       
        if(response.has("result_code")){
            resultCode = response.optString("result_code","field result_code is empty");
            if(resultCode.equalsIgnoreCase("A403")|| resultCode.equalsIgnoreCase("A402")){
                redis.delete(redisKey);
                res = service.CallbackGet(baseUrl+"/invoicing/api/invoice/find-by-order-reference-no/"+id, "", service.getToken());
            }
        }
        if(response.has("result")){
            result = response.getBoolean("result");
            
        }
        if(response.has("result_message")){
            resultMsg = response.optString("result_message" , "field result_message is empty");
            
        }
        if(result){
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "SUCCESS");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }else{
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "FAILURE");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }

        return new ResponseEntity<>(res,HttpStatus.OK);
    }


    @PostMapping(value="/cancelinvoice/id/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelInvoiceById(@PathVariable String id){

        LogFormatter logFormatter = new LogFormatter();
        HashMap<String,String> hmLog  = new HashMap<>();
        hmLog.put(LogFormatterKeys.service_name.getKey(), "KHQRInvoice");
        hmLog.put(LogFormatterKeys.method_name.getKey(), "callback");
        hmLog.put(LogFormatterKeys.channel.getKey(), "");
        hmLog.put(LogFormatterKeys.request_id.getKey(), id);
        hmLog.put(LogFormatterKeys.request_plan.getKey(), "");
        hmLog.put(LogFormatterKeys.account_id.getKey(), "");
        hmLog.put(LogFormatterKeys.error_code.getKey(), "");
        hmLog.put(LogFormatterKeys.error_message.getKey(), "");
        hmLog.put(LogFormatterKeys.client_ip.getKey(), "");
        hmLog.put(LogFormatterKeys.uuid.getKey(), "");
        hmLog.put(LogFormatterKeys.action.getKey(), "request");
        hmLog.put(LogFormatterKeys.step.getKey(), "1");
        hmLog.put(LogFormatterKeys.nei.getKey(), "wing");
        hmLog.put(LogFormatterKeys.api.getKey(),  "cancelinvoice");
        hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
        logger.info(logFormatter.getLogMessage(hmLog));

        String resultCode="";
        String resultMsg="";
        boolean result = false;
        String res = service.Callback(baseUrl+"/invoicing/api/invoice/cancel-by-id/"+id, "", service.getToken());
        JSONObject response  = new JSONObject(res);

        if(response.has("result_code")){
            resultCode = response.optString("result_code","field result_code is empty");
            if(resultCode.equalsIgnoreCase("A403")|| resultCode.equalsIgnoreCase("A402")){
                redis.delete(redisKey);
                res = service.Callback(baseUrl+"/invoicing/api/invoice/cancel-by-id/"+id, "", service.getToken());
            }
        }
        if(response.has("result")){
            result = response.getBoolean("result");
            
        }
        if(response.has("result_message")){
            resultMsg = response.optString("result_message" , "field result_message is empty");
            
        }
        if(result){
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "SUCCESS");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }else{
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "FAILURE");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }

        return new ResponseEntity<>(res,HttpStatus.OK);
    }
    @PostMapping(value="/cancelinvoice/order-refer-no/{id}" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> cancelInvoiceByRefer(@PathVariable String id){

        LogFormatter logFormatter = new LogFormatter();
        HashMap<String,String> hmLog  = new HashMap<>();
        hmLog.put(LogFormatterKeys.service_name.getKey(), "KHQRInvoice");
        hmLog.put(LogFormatterKeys.method_name.getKey(), "callback");
        hmLog.put(LogFormatterKeys.channel.getKey(), "");
        hmLog.put(LogFormatterKeys.request_id.getKey(), id);
        hmLog.put(LogFormatterKeys.request_plan.getKey(), "");
        hmLog.put(LogFormatterKeys.account_id.getKey(), "");
        hmLog.put(LogFormatterKeys.error_code.getKey(), "");
        hmLog.put(LogFormatterKeys.error_message.getKey(), "");
        hmLog.put(LogFormatterKeys.client_ip.getKey(), "");
        hmLog.put(LogFormatterKeys.uuid.getKey(), "");
        hmLog.put(LogFormatterKeys.action.getKey(), "request");
        hmLog.put(LogFormatterKeys.step.getKey(), "1");
        hmLog.put(LogFormatterKeys.nei.getKey(), "wing");
        hmLog.put(LogFormatterKeys.api.getKey(),  "cancelinvoice");
        hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
        logger.info(logFormatter.getLogMessage(hmLog));

        String resultCode="";
        String resultMsg="";
        boolean result = false;
        String res = service.Callback(baseUrl+"/invoicing/api/invoice/cancel-by-order-reference-no/"+id, "", service.getToken());
        JSONObject response  = new JSONObject(res);

        if(response.has("result_code")){
            resultCode = response.optString("result_code","field result_code is empty");
            if(resultCode.equalsIgnoreCase("A403")|| resultCode.equalsIgnoreCase("A402")){
                redis.delete(redisKey);
                res = service.Callback(baseUrl+"/invoicing/api/invoice/cancel-by-order-reference-no/"+id, "", service.getToken());
            }
        }
        if(response.has("result")){
            result = response.getBoolean("result");
            
        }
        if(response.has("result_message")){
            resultMsg = response.optString("result_message" , "field result_message is empty");
            
        }
        if(result){
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "SUCCESS");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }else{
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "FAILURE");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }

        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    @PostMapping(value="/createinvoice" , produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createInvoice(@RequestBody String requestBody) throws Exception{
        
        JSONObject json = new JSONObject(requestBody);
        String accountId = json.getJSONObject("customer").getJSONObject("payload").optString("customer_id","");
        String data = "{\r\n"
				+ "    \"callback_auth_url\": \""+baseCellcardUrl+"/token\",\r\n"
				+ "    \"callback_auth_user\": \"gwAmHWB8bZl7P3eGdSfRlWa6Qqsa\",\r\n"
				+ "    \"callback_auth_password\": \"QgBxwWkBAmgfbnbTNWHi5Uy6YGIa\",\r\n"
				+ "    \"callback_auth_grant_type\": \"client_credentials\",\r\n"
				+ "    \"callback_auth_data_option\": \"HEADER\"\r\n"
				+ "}";
        String encrypted = AESUtils.encrypt(data, crcKey);
        String gatewaySettingString = "{\r\n"
        + "    \"callback_url\": \""+baseCellcardUrl+"/bill-payment/v1/pay/%s\",\r\n"
        + "    \"callback_auth_type\": \"OAUTH\",\r\n"
        + "    \"callback_auth_data\": \"%s\"\r\n"
        + "}";
        JSONObject gatewaySetting = new JSONObject(gatewaySettingString.formatted(accountId,encrypted));
        json.put("gateway_setting", gatewaySetting);
        String orderReferenceNo = json.getString("order_reference_no");
        String currency = json.getString("currency");
        double total = json.getDouble("total");
        String resultCode="";
        String resultMsg="";
        boolean result = false;
        json.put("crc", generateCRC(orderReferenceNo+"|"+currency+"|"+formatNumber(String.valueOf(total),"#.00")));
      
       
        LogFormatter logFormatter = new LogFormatter();
        HashMap<String,String> hmLog  = new HashMap<>();
        hmLog.put(LogFormatterKeys.service_name.getKey(), "KHQRInvoice");
        hmLog.put(LogFormatterKeys.method_name.getKey(), "callback");
        hmLog.put(LogFormatterKeys.channel.getKey(), "");
        hmLog.put(LogFormatterKeys.request_id.getKey(), orderReferenceNo);
        hmLog.put(LogFormatterKeys.request_plan.getKey(), "");
        hmLog.put(LogFormatterKeys.account_id.getKey(), "");
        hmLog.put(LogFormatterKeys.error_code.getKey(), "");
        hmLog.put(LogFormatterKeys.error_message.getKey(), json.toString());
        hmLog.put(LogFormatterKeys.client_ip.getKey(), "");
        hmLog.put(LogFormatterKeys.uuid.getKey(), "");
        hmLog.put(LogFormatterKeys.action.getKey(), "request");
        hmLog.put(LogFormatterKeys.step.getKey(), "1");
        hmLog.put(LogFormatterKeys.nei.getKey(), "wing");
        hmLog.put(LogFormatterKeys.api.getKey(),  "createinvoice");
        hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
        logger.info(logFormatter.getLogMessage(hmLog));
     
        String res = service.Callback(baseUrl+"/invoicing/api/invoice/ext/create", json.toString(), service.getToken());
        JSONObject response  = new JSONObject(res);

        if(response.has("result_code")){
            resultCode = response.optString("result_code","field result_code is empty");
            if(resultCode.equalsIgnoreCase("A403")|| resultCode.equalsIgnoreCase("A402")){
                redis.delete(redisKey);
                res = service.Callback(baseUrl+"/invoicing/api/invoice/ext/create", json.toString(), service.getToken());
            }
        }

        if(response.has("result")){
            result = response.getBoolean("result");
            
        }
        if(response.has("result_message")){
            resultMsg = response.optString("result_message" , "field result_message is empty");
            
        }
        if(result){
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "SUCCESS");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }else{
            hmLog.put(LogFormatterKeys.action.getKey(), "response");
            hmLog.put(LogFormatterKeys.result.getKey(), "FAILURE");
            hmLog.put(LogFormatterKeys.error_code.getKey(), resultCode);
            hmLog.put(LogFormatterKeys.error_message.getKey(), resultMsg);
            hmLog.put(LogFormatterKeys.transaction_time.getKey(), String.valueOf(formatter.format(new Date())));
            logger.info(logFormatter.getLogMessage(hmLog));
        }

        return new ResponseEntity<>(res,HttpStatus.OK);
    }

    String generateCRC(String data) throws NoSuchAlgorithmException{
       
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        md.update(crcKey.getBytes());
        byte[] hash = md.digest(data.getBytes(StandardCharsets.UTF_8));

        return Base64.getEncoder().encodeToString(hash);
    }

    public String formatNumber(String  valueToFormat,String pattern) {
		double d = 0;
		
		DecimalFormat dm = new DecimalFormat();
		dm.setRoundingMode(RoundingMode.DOWN);
		try {
			d = Double.parseDouble(valueToFormat);
		
			dm = new DecimalFormat(pattern);
		} catch(Exception e) {
			d = 0;
		}
		
		return dm.format(d);
	}

}