package com.siewe_rostand.tvcam.Discount.service;

import com.siewe_rostand.tvcam.Discount.dto.DiscountMapper;
import com.siewe_rostand.tvcam.Discount.dto.DiscountRequest;
import com.siewe_rostand.tvcam.Discount.dto.DiscountResponse;
import com.siewe_rostand.tvcam.Discount.model.Discount;
import com.siewe_rostand.tvcam.Discount.repository.DiscountRepository;
import com.siewe_rostand.tvcam.Payment.model.enumeration.PaymentFrequency;
import com.siewe_rostand.tvcam.common.constraints.validator.ObjectsValidator;
import com.siewe_rostand.tvcam.shared.Exceptions.EntityNotFoundException;
import com.siewe_rostand.tvcam.shared.HttpResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service Discount
 * 
 * @author rostand
 * @project tv-cam
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;
    private final ObjectsValidator<DiscountRequest> validator;

    @Override
    @Transactional
    public DiscountResponse createDiscount(DiscountRequest request) {
        log.info("Création d'une nouvelle remise pour la fréquence: {}", request.getPaymentFrequency());
        validator.validate(request);

        // Vérifier qu'il n'existe pas déjà une remise pour cette fréquence
        List<Discount> existingDiscounts = discountRepository.findByPaymentFrequency(request.getPaymentFrequency());
        if (!existingDiscounts.isEmpty()) {
            throw new RuntimeException("Une remise existe déjà pour cette fréquence de paiement");
        }

        Discount discount = discountMapper.toDiscount(request);
        Discount savedDiscount = discountRepository.save(discount);

        log.info("Remise créée avec succès pour la fréquence: {}", savedDiscount.getPaymentFrequency());
        return discountMapper.toResponse(savedDiscount);
    }

    @Override
    @Transactional
    public DiscountResponse updateDiscount(Long discountId, DiscountRequest request) {
        log.info("Mise à jour de la remise ID: {}", discountId);
        validator.validate(request);

        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("Remise non trouvée avec l'ID: " + discountId));

        discountMapper.updateDiscountFromRequest(request, discount);
        Discount updatedDiscount = discountRepository.save(discount);

        return discountMapper.toResponse(updatedDiscount);
    }

    @Override
    @Transactional
    public HttpResponse<Object> deleteDiscount(Long discountId) {
        log.info("Suppression de la remise ID: {}", discountId);

        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("Remise non trouvée avec l'ID: " + discountId));

        discountRepository.delete(discount);

        return HttpResponse.builder().success(true)
                .message("Remise supprimée avec succès")
                .status(HttpStatus.OK.getReasonPhrase())
                .statusCode(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountById(Long discountId) {
        Discount discount = discountRepository.findById(discountId)
                .orElseThrow(() -> new EntityNotFoundException("Remise non trouvée avec l'ID: " + discountId));

        return discountMapper.toResponse(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getAllActiveDiscounts() {
        log.debug("Récupération de toutes les remises actives");
        List<Discount> activeDiscounts = discountRepository.findByIsActiveTrue();
        return activeDiscounts.stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountByPaymentFrequency(PaymentFrequency paymentFrequency) {
        log.debug("Recherche de remise pour la fréquence: {}", paymentFrequency);

        List<Discount> discounts = discountRepository.findByPaymentFrequency(paymentFrequency);
        if (discounts.isEmpty()) {
            throw new EntityNotFoundException("Aucune remise trouvée pour la fréquence: " + paymentFrequency);
        }

        // Prendre la première remise active trouvée
        Discount discount = discounts.stream()
                .filter(Discount::getIsActive)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(
                        "Aucune remise active trouvée pour la fréquence: " + paymentFrequency));

        return discountMapper.toResponse(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscountedAmount(BigDecimal originalAmount, PaymentFrequency paymentFrequency) {
        log.debug("Calcul du montant après remise pour: {} avec fréquence: {}", originalAmount, paymentFrequency);

        try {
            DiscountResponse discountResponse = getDiscountByPaymentFrequency(paymentFrequency);

            // Créer un objet Discount temporaire pour les calculs
            Discount discount = Discount.builder()
                    .discountPercentage(discountResponse.getDiscountPercentage())
                    .isActive(discountResponse.getIsActive())
                    .minimumAmount(discountResponse.getMinimumAmount())
                    .maximumDiscountAmount(discountResponse.getMaximumDiscountAmount())
                    .build();

            return discount.calculateDiscountedAmount(originalAmount);
        } catch (EntityNotFoundException e) {
            log.debug("Aucune remise trouvée pour la fréquence: {}, retour du montant original", paymentFrequency);
            return originalAmount;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateDiscountAmount(BigDecimal originalAmount, PaymentFrequency paymentFrequency) {
        log.debug("Calcul du montant de remise pour: {} avec fréquence: {}", originalAmount, paymentFrequency);

        try {
            DiscountResponse discountResponse = getDiscountByPaymentFrequency(paymentFrequency);

            // Créer un objet Discount temporaire pour les calculs
            Discount discount = Discount.builder()
                    .discountPercentage(discountResponse.getDiscountPercentage())
                    .isActive(discountResponse.getIsActive())
                    .minimumAmount(discountResponse.getMinimumAmount())
                    .maximumDiscountAmount(discountResponse.getMaximumDiscountAmount())
                    .build();

            return discount.calculateDiscount(originalAmount);
        } catch (EntityNotFoundException e) {
            log.debug("Aucune remise trouvée pour la fréquence: {}", paymentFrequency);
            return BigDecimal.ZERO;
        }
    }
}
