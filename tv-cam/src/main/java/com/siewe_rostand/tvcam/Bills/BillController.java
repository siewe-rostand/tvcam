package com.siewe_rostand.tvcam.Bills;

import com.siewe_rostand.tvcam.exceptions.ResourceNotFoundException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Map;


/**
 * @author rostand
 * @project tv-cam
 */

@RestController
@RequestMapping("api/vi/bill")
public class BillController {
    private final Logger logger = LoggerFactory.getLogger(BillController.class);

    private final BillServices billServices;

    public BillController(BillServices billServices) {
        this.billServices = billServices;
    }

    @PostMapping("/save")
    public ResponseEntity<HttpResponse> saveBill(@RequestBody BillSDto billSDto)throws ResourceNotFoundException {
        logger.debug("BillController:::saveBills {}",billSDto);
        BillSDto billSDto1 = new BillSDto().CreateDTO(billServices.save(billSDto));
        return ResponseEntity.created(URI.create("save_bill")).body(
                HttpResponse.builder()
                        .timestamp(LocalDateTime.now()).message("Bills created successfully")
                        .status(HttpStatus.CREATED).statusCode(HttpStatus.CREATED.value()).data(Map.of("bill",billSDto1))
                        .build()
        );
    }

}
