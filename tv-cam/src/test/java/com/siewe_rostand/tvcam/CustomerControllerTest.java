//package com.siewe_rostand.tvcam;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.siewe_rostand.tvcam.Customers.*;
//
//import org.junit.jupiter.api.Test;
//
//
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.ResultActions;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//
//import static org.hamcrest.CoreMatchers.is;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertTrue;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//
//@WebMvcTest(CustomerController.class)
//public class CustomerControllerTest {
//
//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CustomerServiceImpl customerService;
//
//    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Test
//    public void givenCustomerObject_whenCreateCustomer_thenReturnSaveCustomer() throws Exception{
//        //given precondition
//
//        CustomersDto customersDto = new CustomersDto();
//        customersDto.setAddress("a cote de la solidarite");
//        customersDto.setName("edouard");
//        customersDto.setTelephone("673423");
//        customersDto.setIsSuspended(false);
//        customersDto.setIsActive(true);
//        customersDto.setHasPaid(true);
//        customersDto.setHasDebt(true);
//        System.out.println("======================================"+customersDto);
//
////        given(customerService.save(any(CustomersDto.class))).willAnswer((invocation)->invocation.getArgument(0));
//        Mockito.when(customerService.save(Mockito.any(CustomersDto.class))).thenReturn(new Customers().toMap(customersDto));
//
//        // when - action or behaviour we are to test
//        ResultActions resultActions = mockMvc.perform(post("/api/v1/customers")
//        .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(customersDto)));
//
//        assertEquals(MethodArgumentNotValidException.class,resultActions.andReturn().getResolvedException().getClass());
//        assertTrue(resultActions.andReturn().getResolvedException().getMessage().contains("'make' field was empty"));
//
//        // then - verify the result or output using assert statements
////        resultActions.andDo(print()).andExpect(status().isCreated()).andExpect(
////                jsonPath("$.name",is(customersDto.getName()))).andExpect(jsonPath("$.address",is(customersDto.getAddress())))
////                .andExpect(jsonPath("$.telephone",is(customersDto.getTelephone()))).andExpect(jsonPath("$.isActive",is(customersDto.getIsActive())))
////                .andExpect(jsonPath("$.hasDebt",is(customersDto.getHasDebt()))).andExpect(jsonPath("$.hasPaid",is(customersDto.getHasPaid()))).
////                andExpect(jsonPath("$.isSuspended",is(customersDto.getIsSuspended())));
//
//
//    }
//}
