package com.hmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.util.List;

@SpringBootTest
public class ItemApplicationTests {
    @Autowired
    private DiscoveryClient discoveryClient;

    public void contextLoads() {
    }

    @Test
    void testNacosServiceRegister() {
        discoveryClient.getServices().forEach(System.out::println);
        List<ServiceInstance> instances = discoveryClient.getInstances("cart-service");
        instances.forEach(instance -> System.out.println(instance.getUri()));
    }
}
