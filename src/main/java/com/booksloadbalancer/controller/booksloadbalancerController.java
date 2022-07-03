package com.booksloadbalancer.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.tomcat.jni.Global;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.RoundRobinLoadBalancer;
import org.springframework.cloud.loadbalancer.support.LoadBalancerClientFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.booksloadbalancer.authclient.booksloadbalancerAuthclient;
import com.booksloadbalancer.entity.Books;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@RestController
public class booksloadbalancerController {
	@Autowired
	LoadBalancerClientFactory clientFactory;
	@Autowired
	RestTemplate restTemplate;
	//@Autowired
	//OAuthService oauth;
	@Autowired
	booksloadbalancerAuthclient authToken;
	
	@Value("${booksservice.instance}")
	public String bookservice;
	
	@CircuitBreaker(name="backup",fallbackMethod="fallbackAddBooks")
	@PostMapping("/books/add")
	public String createOrder(@RequestBody Books books) {
		RoundRobinLoadBalancer lb= clientFactory.getInstance(bookservice,RoundRobinLoadBalancer.class);
		ServiceInstance instance =lb.choose().block().getServer();
		String url= "http://"+instance.getHost()+":"+instance.getPort()+"/books/add";
		
		HttpHeaders headers = new HttpHeaders();
		
		String oauthurl="http://"+instance.getHost()+":"+instance.getPort()+"/oauth/token";
		String oauthToken=authToken.getOAuthToken(oauthurl);
		System.out.println("oauthToken: "+oauthToken);
		headers.add("Authorization","Bearer "+oauthToken );
		
		HttpEntity<Books> request = new HttpEntity<Books>(books,headers);
		
		
		//ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
		
		ResponseEntity<String> result=restTemplate.postForEntity(url, request, String.class);
		return result.getBody();
	}
	
	public String fallbackAddBooks(Exception ex){
		return " Some issues in Book Loadbalncer's Add function.";
	}
	
	@CircuitBreaker(name="backup",fallbackMethod="fallbackFetchBookByID")
	@GetMapping("/books/fetch/{id}")
	public ResponseEntity<Books> fetchOrderById(@PathVariable("id") String id) {
		RoundRobinLoadBalancer lb= clientFactory.getInstance(bookservice,RoundRobinLoadBalancer.class);
		ServiceInstance instance =lb.choose().block().getServer();
		System.out.println("Instance :"+instance);
		String url= "http://"+instance.getHost()+":"+instance.getPort()+"/books/fetch/"+id;
		
HttpHeaders headers = new HttpHeaders();
		
		String oauthurl="http://"+instance.getHost()+":"+instance.getPort()+"/oauth/token";
		String oauthToken=authToken.getOAuthToken(oauthurl);
		System.out.println("oauthToken: "+oauthToken);
		headers.add("Authorization","Bearer "+oauthToken );
		
		HttpEntity<String> request = new HttpEntity<String>(headers);		
		ResponseEntity<Books> book = restTemplate.exchange(url, HttpMethod.GET, request, Books.class);

		
		//Books book=restTemplate.getForObject(url, Books.class);
		return book;
	}
	
	public ResponseEntity<Books> fallbackFetchBookByID(Exception ex){
		return ResponseEntity.ok(new Books());
	}
	

	@CircuitBreaker(name="backup",fallbackMethod="fallbackFetchBooks")
	@GetMapping("/books/fetch")
	public ResponseEntity<String> fetchOrder() {
		RoundRobinLoadBalancer lb= clientFactory.getInstance(bookservice,RoundRobinLoadBalancer.class);
		ServiceInstance instance =lb.choose().block().getServer();
		System.out.println("Booksservice :"+bookservice);
		System.out.println("Instance :"+instance);
		String url= "http://"+instance.getHost()+":"+instance.getPort()+"/books/fetch";
		
		//String token=oauth.getOAuthToken(clientId, clientSecret);
		

		HttpHeaders headers = new HttpHeaders();
		
		String oauthurl="http://"+instance.getHost()+":"+instance.getPort()+"/oauth/token";
		String oauthToken=authToken.getOAuthToken(oauthurl);
		System.out.println("oauthToken: "+oauthToken);
		headers.add("Authorization","Bearer "+oauthToken );
		
		HttpEntity<String> request = new HttpEntity<String>(headers);		
		

		ResponseEntity<String> book = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
		
		//ResponseEntity<String> book=restTemplate.getForObject(url, String.class);
		return book;
	}
	
	public ResponseEntity<String> fallbackFetchBooks(Exception ex){
		return ResponseEntity.ok(" Some issues in Book Loadbalncer's Fetch function.");
	}
	
	@CircuitBreaker(name="backup",fallbackMethod="fallbackDeleteBooks")
	@DeleteMapping("/books/delete/{id}")
	public String deleteOrder(@PathVariable("id") String id) {
		RoundRobinLoadBalancer lb= clientFactory.getInstance(bookservice,RoundRobinLoadBalancer.class);
		ServiceInstance instance =lb.choose().block().getServer();
		System.out.println("Instance :"+instance);
		String url= "http://"+instance.getHost()+":"+instance.getPort()+"/books/delete/"+id;
		
		
		 
		 
		 HttpHeaders headers = new HttpHeaders();
			
			String oauthurl="http://"+instance.getHost()+":"+instance.getPort()+"/oauth/token";
			String oauthToken=authToken.getOAuthToken(oauthurl);
			System.out.println("oauthToken: "+oauthToken);
			headers.add("Authorization","Bearer "+oauthToken );
			
			HttpEntity<String> request = new HttpEntity<String>(headers);	
		 
		 
		return restTemplate.exchange(url,HttpMethod.DELETE,request, String.class).getBody();
	}
	
	public String fallbackDeleteBooks(Exception ex){
		return " Some issues in Book Loadbalncer's Delete function";
	}
	
	
	@CircuitBreaker(name="backup",fallbackMethod="fallbackEditBooks")
	@PutMapping("/books/edit")
	public String editBooks(@RequestBody Books books) {
		RoundRobinLoadBalancer lb= clientFactory.getInstance(bookservice,RoundRobinLoadBalancer.class);
		ServiceInstance instance =lb.choose().block().getServer();
		System.out.println("Instance :"+instance);
		String url= "http://"+instance.getHost()+":"+instance.getPort()+"/books/edit";

		 HttpHeaders headers = new HttpHeaders();
			
			String oauthurl="http://"+instance.getHost()+":"+instance.getPort()+"/oauth/token";
			String oauthToken=authToken.getOAuthToken(oauthurl);
			System.out.println("oauthToken: "+oauthToken);
			headers.add("Authorization","Bearer "+oauthToken );
			
			//HttpEntity<String> request = new HttpEntity<String>(headers);	
		 



		 HttpEntity<Books> httpEntity = new HttpEntity<Books>(books,headers);
		 restTemplate.exchange(url,HttpMethod.PUT,httpEntity, Void.class);
		
		
		return "Updation done from Books EditLoadBanlancer";
	}
	
	
	public String fallbackEditBooks(Exception ex){
		return " Some issues in Book Loadbalncer's Edit function";
	}
	
	

	

}
