package www.cellcard.com.kh.KHQRPrepaidInvoice.service;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import www.cellcard.com.kh.KHQRPrepaidInvoice.utility.LogFormatter;
import www.cellcard.com.kh.KHQRPrepaidInvoice.utility.LogFormatterKeys;

@Service
public class KHQRService {


    @Value("${user}")
    String username;
    @Value("${password}")
    String password;
    @Value("${grant_type}")
    String grant_type;
    @Value("${client_id}")
    String client_id;
    @Value("${client_secret}")
    String client_secret;
    @Value("${base-url}")
    String baseUrl;
    @Value("${redis-key}")
    String redisKey;

    @Autowired
    RedisTemplate<String,String> redis;
    public String  Callback(String url, String payload, String token){
        // System.out.println("Url : "+url);
        // System.out.println("Payload : "+payload);
        // System.out.println("Token : "+token);
        WebClient webClientBuilder = WebClient.builder().build();
        return  webClientBuilder
                .post()
                .uri(url)
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .bodyValue(payload)
                .exchangeToMono(cr -> cr.bodyToMono(String.class))
                .timeout(Duration.ofMillis(300000)).block();


    }

    public String  CallbackGet(String url, String payload, String token){
        // System.out.println("Url : "+url);
        // System.out.println("Payload : "+payload);
        // System.out.println("Token : "+token);
        WebClient webClientBuilder = WebClient.builder().build();
        return  webClientBuilder
                .get()
                .uri(url)
                .header("Authorization", "Bearer "+token)
                .header("Content-Type", "application/json")
                .exchangeToMono(cr -> cr.bodyToMono(String.class))
                .timeout(Duration.ofMillis(300000)).block();


    }

    public  String getToken(){
        var token = redis.opsForValue().get(redisKey);
        if(token == null){
            JSONObject json = new JSONObject();
            json.put("username",username);
            json.put("password",password);
            json.put("grant_type",grant_type);
            json.put("client_secret",client_secret);
            json.put("client_id",client_id);
            WebClient webClientBuilder = WebClient.builder().build();
            String res = webClientBuilder
                .post()
                .uri(baseUrl+"/identity/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(json.toString())
                .exchangeToMono(cr -> cr.bodyToMono(String.class))
                .timeout(Duration.ofMillis(300000)).block();
            System.out.println(res);
            System.out.println(baseUrl+"/identity/login");
            System.out.println(json.toString());
            json = new JSONObject(res);
            if(json.has("body") && json.getJSONObject("body").has("access_token")){
                redis.opsForValue().set(redisKey, json.getJSONObject("body").getString("access_token"));
                redis.expire(redisKey, json.getJSONObject("body").getInt("expires_in")-10, TimeUnit.SECONDS);
                return json.getJSONObject("body").getString("access_token");
            }
        }else{
            return token;
        }
        return "ERROR";
        
    }
}
