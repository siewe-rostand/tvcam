package com.siewe_rostand.tvcam.Bills.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.siewe_rostand.tvcam.Bills.dto.BillRequest;
import com.siewe_rostand.tvcam.Bills.dto.BillResponse;
import com.siewe_rostand.tvcam.Bills.services.BillServices;
import com.siewe_rostand.tvcam.common.exceptions.ApiException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import com.siewe_rostand.tvcam.shared.PaginatedResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.time.LocalDateTime.now;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;


/**
 * @author rostand
 * @project tv-cam
 */

@RestController
@RequestMapping("bills")
public class BillController {
    private final Logger logger = LoggerFactory.getLogger(BillController.class);

    private final BillServices billServices;

    public BillController(BillServices billServices) {
        this.billServices = billServices;
    }

    @PostMapping("/save")
    public ResponseEntity<HttpResponse<Object>> saveBill(@RequestBody BillRequest request) {
        logger.debug("BillController:::saveBills {}", request);

        ObjectMapper objectMapper = new ObjectMapper();
        BillResponse response = billServices.generateBills(request);
        Map<String, Object> data = objectMapper
                .convertValue(response, new TypeReference<>() {
                });
        return ResponseEntity.created(URI.create("save_bill")).body(
                HttpResponse.builder().success(true)
                        .timestamp(now()).message("Bills created successfully")
                        .status(CREATED.getReasonPhrase()).statusCode(CREATED.value()).data(data)
                        .build()
        );
    }

    @PostMapping("/generate")
    public ResponseEntity<HttpResponse<Object>> generateBillsForSelectedCustomers(
            @RequestBody List<Long> customerIds, 
            @RequestParam Boolean shouldGenerate) {
        
        logger.info("BillController:::generateBillsForSelectedCustomers - Customer IDs: {}, Should Generate: {}", 
                   customerIds, shouldGenerate);
        
        // Validation des paramètres d'entrée
        if (customerIds == null || customerIds.isEmpty()) {
            logger.warn("Aucun ID de client fourni pour la génération de factures");
            return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                    .success(false)
                    .timestamp(now())
                    .message("Aucun client sélectionné pour la génération de factures")
                    .status("BAD_REQUEST")
                    .statusCode(400)
                    .build()
            );
        }
        
        if (shouldGenerate == null) {
            shouldGenerate = false; // Valeur par défaut
        }
        
        try {
            // Génération des factures
            List<BillResponse> generatedBills = billServices.generateBillsForSelectedCustomers(customerIds, shouldGenerate);
            
            // Statistiques de génération
            int totalRequested = customerIds.size();
            int totalGenerated = generatedBills.size();
            int skipped = totalRequested - totalGenerated;
            
            String message = String.format(
                "Génération de factures terminée: %d facture(s) générée(s) sur %d client(s) demandé(s)%s",
                totalGenerated, 
                totalRequested,
                skipped > 0 ? String.format(" (%d client(s) ignoré(s))", skipped) : ""
            );
            
            logger.info("Génération réussie: {} factures générées pour {} clients", totalGenerated, totalRequested);
            
            // Construction de la réponse enrichie
            Map<String, Object> responseData = Map.of(
                "bills", generatedBills,
                "statistics", Map.of(
                    "totalRequested", totalRequested,
                    "totalGenerated", totalGenerated,
                    "skipped", skipped,
                    "generationDate", now().toString(),
                    "shouldGenerate", shouldGenerate
                )
            );
            
            return ResponseEntity.status(CREATED).body(
                HttpResponse.builder()
                    .success(true)
                    .timestamp(now())
                    .message(message)
                    .data(responseData)
                    .status(CREATED.getReasonPhrase())
                    .statusCode(CREATED.value())
                    .build()
            );
            
        } catch (ApiException e) {
            logger.error("Erreur API lors de la génération de factures: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Erreur inattendue lors de la génération de factures: {}", e.getMessage(), e);
            throw new ApiException(
                "Une erreur inattendue s'est produite lors de la génération des factures", 
                "Erreur de génération de factures"
            );
        }
    }

    @GetMapping()
    public ResponseEntity<PaginatedResponse> getAllBills(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @RequestParam(name = "sortBy", defaultValue = "createdAt") String sortBy,
            @RequestParam(name = "direction", defaultValue = "desc") String direction,
            @RequestParam(name = "name", defaultValue = "") String name) {
        PaginatedResponse response = billServices.findAll(page, size, sortBy, direction, name);
        return ResponseEntity.status(OK).body(response);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<HttpResponse<Object>> getBillsByCustomerId(@PathVariable(name = "customerId") Long customerId) {
        logger.debug("BillController::getBillsByCustomerId {}", customerId);
        HttpResponse<Object> response = billServices.findCustomerBills(customerId);

        return ResponseEntity.status(OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpResponse<Object>> deleteBill(@PathVariable(name = "id") Long id) {
        logger.debug("BillController:::deleteBill {}", id);
        HttpResponse<Object> response = billServices.delete(id);
        return ResponseEntity.ok().body(response);
    }

    /**
     * Récupère les factures pour impression (format 2 par page)
     */
    @PostMapping("/print")
    public ResponseEntity<HttpResponse<Object>> getBillsForPrint(@RequestBody List<Long> billIds) {
        logger.info("BillController:::getBillsForPrint - Bill IDs: {}", billIds);
        
        if (billIds == null || billIds.isEmpty()) {
            logger.warn("Aucun ID de facture fourni pour l'impression");
            return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                    .success(false)
                    .timestamp(now())
                    .message("Aucune facture sélectionnée pour l'impression")
                    .status("BAD_REQUEST")
                    .statusCode(400)
                    .build()
            );
        }
        
        try {
            // Récupération des factures pour impression
            List<BillResponse> billsForPrint = billServices.getBillsForPrint(billIds);
            
            // Organisation en pages (2 factures par page)
            List<List<BillResponse>> pages = organizeBillsInPages(billsForPrint);
            
            Map<String, Object> printData = Map.of(
                "bills", billsForPrint,
                "pages", pages,
                "totalBills", billsForPrint.size(),
                "totalPages", pages.size(),
                "printFormat", "2_per_page",
                "printDate", now().toString()
            );
            
            logger.info("Factures préparées pour impression: {} factures, {} pages", 
                       billsForPrint.size(), pages.size());
            
            return ResponseEntity.ok(
                HttpResponse.builder()
                    .success(true)
                    .timestamp(now())
                    .message(String.format("%d facture(s) préparée(s) pour impression", billsForPrint.size()))
                    .data(printData)
                    .status(OK.getReasonPhrase())
                    .statusCode(OK.value())
                    .build()
            );
            
        } catch (Exception e) {
            logger.error("Erreur lors de la préparation des factures pour impression: {}", e.getMessage(), e);
            throw new ApiException(
                "Erreur lors de la préparation des factures pour impression", 
                "Erreur d'impression"
            );
        }
    }

    /**
     * Vérifie l'existence de factures pour un mois donné
     */
    @PostMapping("/check-existing")
    public ResponseEntity<HttpResponse<Object>> checkExistingBillsForMonth(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Long> customerIds = (List<Long>) request.get("customerIds");
        String month = (String) request.get("month");
        String year = (String) request.get("year");
        
        logger.info("BillController:::checkExistingBillsForMonth - Customers: {}, Month: {}, Year: {}", 
                   customerIds, month, year);
        
        try {
            boolean hasExistingBills = billServices.checkExistingBillsForMonth(customerIds, month, year);
            
            Map<String, Object> responseData = Map.of(
                "hasExistingBills", hasExistingBills,
                "month", month,
                "year", year,
                "customerCount", customerIds != null ? customerIds.size() : 0,
                "checkDate", now().toString()
            );
            
            return ResponseEntity.ok(
                HttpResponse.builder()
                    .success(true)
                    .timestamp(now())
                    .message(hasExistingBills ? 
                        "Des factures existent déjà pour ce mois" : 
                        "Aucune facture existante pour ce mois")
                    .data(responseData)
                    .status(OK.getReasonPhrase())
                    .statusCode(OK.value())
                    .build()
            );
            
        } catch (Exception e) {
            logger.error("Erreur lors de la vérification des factures existantes: {}", e.getMessage(), e);
            throw new ApiException(
                "Erreur lors de la vérification des factures existantes", 
                "Erreur de vérification"
            );
        }
    }

    /**
     * Supprime plusieurs factures en lot
     */
    @DeleteMapping("/batch")
    public ResponseEntity<HttpResponse<Object>> deleteBillsBatch(@RequestBody List<Long> billIds) {
        logger.info("BillController:::deleteBillsBatch - Bill IDs: {}", billIds);
        
        if (billIds == null || billIds.isEmpty()) {
            return ResponseEntity.badRequest().body(
                HttpResponse.builder()
                    .success(false)
                    .timestamp(now())
                    .message("Aucune facture sélectionnée pour la suppression")
                    .status("BAD_REQUEST")
                    .statusCode(400)
                    .build()
            );
        }
        
        try {
            int deletedCount = billServices.deleteBillsBatch(billIds);
            
            return ResponseEntity.ok(
                HttpResponse.builder()
                    .success(true)
                    .timestamp(now())
                    .message(String.format("%d facture(s) supprimée(s) avec succès", deletedCount))
                    .data(Map.of(
                        "deletedCount", deletedCount,
                        "requestedCount", billIds.size(),
                        "deletionDate", now().toString()
                    ))
                    .status(OK.getReasonPhrase())
                    .statusCode(OK.value())
                    .build()
            );
            
        } catch (Exception e) {
            logger.error("Erreur lors de la suppression en lot: {}", e.getMessage(), e);
            throw new ApiException(
                "Erreur lors de la suppression des factures", 
                "Erreur de suppression"
            );
        }
    }

    /**
     * Organise les factures en pages de 2 factures chacune
     */
    private List<List<BillResponse>> organizeBillsInPages(List<BillResponse> bills) {
        List<List<BillResponse>> pages = new ArrayList<>();
        
        for (int i = 0; i < bills.size(); i += 2) {
            List<BillResponse> page = new ArrayList<>();
            page.add(bills.get(i));
            
            if (i + 1 < bills.size()) {
                page.add(bills.get(i + 1));
            }
            
            pages.add(page);
        }
        
        return pages;
    }

}
